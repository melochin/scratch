package scratch.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import scratch.context.RedisContext;
import scratch.controller.RegisterController;
import scratch.controller.UserController;
import scratch.dao.UserDao;
import scratch.exception.AuthenException;
import scratch.model.entity.User;
import scratch.support.service.MailException;
import scratch.support.service.PageBean;

import static scratch.context.RedisContext.*;

@Service
public class UserService {

	private ValueOperations<String, String> valueOperations;
	
	private DaoAuthenticationProvider provider;

	private EmailService emailService;

	private UserDao dao;

    @SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(UserService.class);
	
	private static long TIME_OUT = 10;	//有效期10分钟

	@Autowired
	public UserService(RedisTemplate redisTemplate,
					   DaoAuthenticationProvider provider,
					   EmailService emailService,
					   UserDao userDao) {
		this.provider = provider;
		this.valueOperations = redisTemplate.opsForValue();
		this.emailService = emailService;
		this.dao = userDao;
	}
	
	/**------------------------------查询类服务--------------------------------*/
	
	/** 根据userId查询User */
	public User getById(Long userId) {
		return dao.getById(userId);
	}

	/** 根据用户名查询User */
	public User getByName(String username) {
		User user = new User(username);
		return dao.getByName(user);
	}

	/** 根据username与email查找User */
	public User getByNameAndEmail(String username, String email) {
		return dao.getByNameAndEmail(username, email);
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

	/** 账号认证　*/
	public Authentication authen(String username, String password) {
		// get salt
		User user = getByName(username);
		if(user == null) {
			throw new AuthenException();
		}
		String salt = user.getSalt();
		// check password
		String hashedPassword = BCrypt.hashpw(password, salt);
		Authentication authentication = null;
		try {
			authentication =  provider.authenticate(
					new UsernamePasswordAuthenticationToken(username, hashedPassword));
		} catch (RuntimeException e) {
			throw new AuthenException();
		}
		if(authentication == null) {
			throw new AuthenException();
		}
		return authentication;
	}

	/**------------------------------修改类服务--------------------------------*/
	
	/** 用户注册（注意事务）*/
	@Transactional
	public void save(User user) throws MailException {
		if(isExistByUsername(user.getUsername())) throw new RuntimeException("账号已经存在");
		// 设置默认值
		if(user.getStatus() == null) {
			user.setStatus("0");
		}
		// 新增用户
		encodePassword(user);
		dao.save(user);
		// 发送认证邮件
		// 后台管理添加的用户，可以选择跳过这步骤
		if(user.getStatus().equals("1")) return;
		// 如果邮件发送失败，抛出checked异常，不影响注册事务，可以事后补邮件认证
		try{
			sendActiveMail(user);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MailException(e.getMessage());
		}
		return;
	}

	/** 用户密码更改　*/
	@Transactional
	public void modifyPassword(String username, String oldPassword, String newPassword) {
		Authentication authentication = authen(username, oldPassword);
		if(authentication == null) {
			throw new RuntimeException("密码错误");
		}
		User user = getByName(username);
		user.setPassword(newPassword);
		modify(user);
	}

	/**
	 * 更新用户信息，自动对user.password进行加密
	 * @param u
	 */
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
	
	/**-------用户邮箱认证 ------------*/

	/** 发送用户邮箱认证邮件　*/
	public void sendActiveMail(User user) throws MailException, MessagingException {
		String activeUrl = getActiUrl(user.getUserId());
		emailService.sendUserActiveCode(activeUrl, user.getEmail());
	}

	 /** 修改用户邮箱地址、发送验证邮箱 */
	 @Transactional
	public void modifyEmail(Long userId, String email) throws MailException, MessagingException {
		User user = new User(userId);
		user.setEmail(email);
		modify(user);
		sendActiveMail(user);
	}

	/**
	 * 生成邮箱认证用的URL
	 *
	 * 负责处理该URL的方法
	 * @see RegisterController#activiti(Long, String, RedirectAttributes)
	 *
	 * @param userId
	 * @return
	 */
	public String getActiUrl(Long userId) {
		// 生成认证码
		String confirmCode = BCrypt.hashpw(String.valueOf(userId), BCrypt.gensalt());
		//将认证码放入缓存
		String key = redisKey(USER_AUTH_MAIL,userId);
		valueOperations.set(key, confirmCode, TIME_OUT, TimeUnit.MINUTES);
		//生成账号激活的URL
		String url = MvcUriComponentsBuilder.fromMethodName(
				RegisterController.class, "activiti", userId, confirmCode, null)
				.build().encode().toUriString();
		return url;
	}
	
	/**
	 * 处理邮箱认证,认证成功则更改用户状态
	 *
	 * @param userId
	 * @return -1: redis中不存在值;
	 * 			0: 激活失败;
	 * 			1: 激活成功
	 */
	@Transactional
	public int confirmEmail(Long userId, String confirmCode) {
		// 查找redis中存放的code，找到执行更新操作
		String redisKey = redisKey(USER_AUTH_MAIL, userId);
		String code = valueOperations.get(redisKey);
		if(StringUtils.isEmpty(code) || !code.equals(confirmCode)) {
			return -1;
		}
		// 更新成功，删除redis中存放的值
		int result = dao.updateStatus(userId, "1") == 1 ? 1 : 0;
		if(result == 1) {
			valueOperations.getOperations().delete(redisKey);
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
		String resetCode = BCrypt.hashpw(String.valueOf(userId), BCrypt.gensalt());
		//将加密后的字符串放入缓存
		String key = redisKey(USER_RESET_PSWD, userId);
		valueOperations.set(key, resetCode, TIME_OUT, TimeUnit.MINUTES);
		//生成密码重置的URL
		String url = MvcUriComponentsBuilder.fromMethodName(
				UserController.class, "resetPasswordForm", resetCode, userId, null)
				.build().encode().toUriString();
		return url;		
	}

	/** 重置密码 */
	public boolean resetPassword(Long userId, String newPassword) {
		User user = dao.getById(userId);
		user.setPassword(newPassword);
		modify(user);
		// 需要判断是否更新成功
		valueOperations.getOperations().delete(redisKey(USER_RESET_PSWD, userId));
		return true;
	}

	/**
	 * 判断用户是否具有重新设置密码的权限
	 * @param userId
	 * @param resetCode
	 * @return
	 */
	public boolean canReset(Long userId, String resetCode) {
		// get key and value
		String redisKey = redisKey(USER_RESET_PSWD, userId);
		String code = valueOperations.get(redisKey);
		// 与redis中的code进行校对
		if(StringUtils.isEmpty(code) || !code.equals(resetCode)) {
			return false;
		}
		return true;
	}

	/**
	 * 生成加密密码和盐
	 * @param user
	 */
	public void encodePassword(User user) {
		String password = user.getPassword();
		if(StringUtils.isEmpty(password)) {
			throw new RuntimeException("user's password cant be empty");
		}
		// 生成盐和加密后的密码
		String salt = BCrypt.gensalt();
		String hashedPassword = BCrypt.hashpw(password, salt);
		user.setSalt(salt);
		user.setHashedPassword(hashedPassword);
	}

}
