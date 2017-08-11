package scratch.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import scratch.aspect.PasswordEncode;
import scratch.context.KeyContext;
import scratch.controller.RegisterController;
import scratch.controller.UserController;
import scratch.dao.UserDao;
import scratch.model.User;
import scratch.support.cipher.CipherSupport;
import scratch.support.service.MailException;
import scratch.support.service.PageBean;

@Transactional
@Service
public class UserService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private DaoAuthenticationProvider provider;
	
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(UserService.class);
	
	private static long TIME_OUT = 10;	//有效期10分钟
		
	private static final String ACTIVE = "active_";
	
	private static final String RESET = "reset_";
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private CipherSupport cipher;
	
	@Autowired
	private UserDao dao;
	
	/**------------------------------查询类服务--------------------------------*/
	
	/** 根据userId查询User */
	public User getById(Long userId) {
		return dao.getById(userId);
	}
	
	/** 查询所有User */
	public List<User> list() {
		List<User> userList = dao.list(User.class);
		return userList;
	}
	
	/** 分页查询User */
	public PageBean<User> list(int page, int pageSize) {
		return dao.findAll(page, pageSize);
	}
	
	/** 判断用户名是否存在  */
	public boolean isExistByUsername(String username) {
		return dao.countByName(username) > 0;
	}
	
/*	*//** 根据username与password查找User *//*
	@PasswordEncode
	public User getByNameAndPwd(String username, @PasswordEncode String password) {
		return dao.getByNameAndPwd(username, password);
	}*/
	
	public Authentication authen(String username, String password) {
		User user = getByName(username);
		String salt = user.getSalt(); 
		String hashedPassword = BCrypt.hashpw(password, salt);
		Authentication authentication =  provider.authenticate(
				new UsernamePasswordAuthenticationToken(username, hashedPassword));
		return authentication;
	}
	
	public User getByName(String username) {
		User user = new User(username);
		return dao.getByName(user);
	}
	
	/** 根据username与email查找User */
	public User getByNameAndEmail(String username, String email) {
		return dao.getByNameAndEmail(username, email);
	}
	
	/**------------------------------修改类服务--------------------------------*/
	
	/** 用户注册（注意事务）*/
	@Transactional
	public void save(User user) throws MailException {
		
		if(isExistByUsername(user.getUsername())) throw new RuntimeException("账号已经存在");
		encodePassword(user);
		// 设置默认值
		if(user.getStatus() == null) {
			user.setStatus("0");
		}
		if(user.getRole() == null) {
			user.setRole(null);
		}
		// 新增用户
		dao.save(user);
		//如果用户的状态处于激活，那就不发生邮件
		//后台直接添加的用户可能处于该状态
		if(user.getStatus().equals("1")) return;
		//抛出checked异常，不影响事务
		try{
			sendActiveMail(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MailException(e.getMessage());
		}
		return;
	}
	
	/**	更新密码，并且删除redis中缓存的resetCode*/
	public void modifyPasswordAndDeleteResetCode(Long userId, @PasswordEncode String password) {
		User user = new User(userId);
		user.setPassword(password);
		encodePassword(user);
		// 注意：内部调用的方式是原生的，不是aop代理之后的modify
		modify(user);
		String redisKey = RESET + userId;
		redisTemplate.opsForValue().getOperations().delete(redisKey);
	}
	
	/** 更新用户信息，自动对user.password进行加密 */
	@Transactional
	public void modify(User u) {
		if(StringUtils.isEmpty(u.getPassword())) {
			u.setHashedPassword(null);
		} else {
			encodePassword(u);
		}
		dao.update(u, u.getUserId());
	}
	
	@Transactional
	public void deleteById(Long id) {
		dao.remove(User.class, id);
	}
	
	/**-----------------------------邮箱类服务------------------------------*/
	
	/**---------------------------
	 * @throws MessagingException 
	 * @throws MailException */
	public void sendActiveMail(User user) throws MailException, MessagingException {
		String activeUrl = getActiUrl(user.getUserId());
		emailService.sendUserActiveCode(activeUrl, user.getEmail());
	}
	
	
	/**-------用户激活服务 ------------*/
	
	/**	生成激活用的URL */
	public String getActiUrl(Long userId) {
		String encodeStr = encrypt(userId, KeyContext.USER_ACTIVE);
		//将加密后的字符串放入缓存
		redisTemplate.opsForValue()
			.set(ACTIVE + userId.toString(), encodeStr, TIME_OUT, TimeUnit.MINUTES);
		//生成账号激活的URL
		String url = MvcUriComponentsBuilder.fromMethodName(
				RegisterController.class, "activiti", userId, encodeStr, null)
				.build()
				.encode()
				.toUriString();
		return url;
	}
	
	/**
	 * @param userId
	 * @return -1: redis中不存在值;	 0: 激活失败; 1: 激活成功
	 */
	public int activi(Long userId, String activiCode) {
		String redisKey = ACTIVE + userId;
		
		if(!redisTemplate.hasKey(redisKey)) {
			return -1;
		}
		
		if(!activiCode.equals(redisTemplate.opsForValue().get(redisKey))) {
			return -1;
		}
		
		int result = 0;
		result = dao.updateStatus(userId, "1") == 1 ? 1 : 0;
		if(result == 1) {
			redisTemplate.opsForValue().getOperations().delete(redisKey);
		}
		return result;
	}
	
	
	/**-------用户密码重置服务 ------------*/
	
	/** 发送重置链接的邮箱 */
	public void sendRestMail(User user) throws MailException, MessagingException {
		String resetUrl = getRestUrl(user.getUserId());
		emailService.sendUserResetPassword(resetUrl, user.getEmail());
		return;
	}
	
	/** 生成重置链接 */
	private String getRestUrl(Long userId) {
		String encodeStr = encrypt(userId, KeyContext.USER_PWD_RESET);
		//将加密后的字符串放入缓存
		redisTemplate.opsForValue()
			.set(RESET + userId.toString(), encodeStr, TIME_OUT, TimeUnit.MINUTES);
		//生成密码重置的URL
		String url = MvcUriComponentsBuilder.fromMethodName(
				UserController.class, "resetPasswordForm", encodeStr, userId, null)
				.build()
				.encode()
				.toUriString();
		return url;		
	}
	
	/** 重置密码 */
	public boolean resetPassword(Long userId, String newPassword) {
		User user = dao.getById(userId);
		user.setPassword(newPassword);
		modify(user);
		// 需要判断是否更新成功
		redisTemplate.opsForValue().getOperations().delete(RESET + userId);
		return true;
	}
	
	
	/** 判断能否重置 */
	public boolean canReset(Long userId, String resetCode) {
		String redisKey = RESET + userId;
		
		if(!redisTemplate.hasKey(redisKey)) {
			return false;
		}
		
		if(!resetCode.equals(redisTemplate.opsForValue().get(redisKey))) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * 加密方式：md5(userId & currentTimeMills & key2, key1)
	 * @param userId
	 * @param key
	 * @return
	 */
	public String encrypt(Long userId, String key) {
		String encodes[] = {userId.toString(), String.valueOf(System.currentTimeMillis()), String.valueOf(UUID.randomUUID())};
		return cipher.encode(key, encodes);
	}
	
	/**
	 * 生成加密密码和盐
	 * @param user
	 * @param hasSalt 
	 */
	public void encodePassword(User user) {
		String password = user.getPassword();
		if(StringUtils.isEmpty(password)) {
			throw new RuntimeException("user's password cant be empty");
		}
		
		String salt = BCrypt.gensalt();
		String hashedPassword = BCrypt.hashpw(password, salt);
		
		user.setSalt(salt);
		user.setHashedPassword(hashedPassword);
	}
	
}
