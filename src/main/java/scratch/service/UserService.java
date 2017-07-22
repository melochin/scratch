package scratch.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import scratch.aspect.PasswordEncode;
import scratch.controller.RegisterController;
import scratch.controller.UserController;
import scratch.dao.UserDao;
import scratch.model.User;
import scratch.support.cipher.CipherSupport;
import scratch.support.service.MailContent;
import scratch.support.service.MailException;
import scratch.support.service.MailService;
import scratch.support.service.PageBean;

@Transactional
@Service
public class UserService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(UserService.class);
	
	private static long TIME_OUT = 10;	//有效期10分钟
	
	private static String[][] KEY = new String[][] {{"51748915", "87741296"}, 
		{"12744597", "26741096"}};
		
	private static final String ACTIVE = "active_";
	
	private static final String RESET = "reset_";
	
	@Autowired
	private MailService mailService;
	
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
	
	/** 根据username与password查找User */
	@PasswordEncode
	public User getByNameAndPwd(String username, @PasswordEncode String password) {
		return dao.getByNameAndPwd(username, password);
	}
	
	/** 根据username与email查找User */
	public User getByNameAndEmail(String username, String email) {
		return dao.getByNameAndEmail(username, email);
	}
	
	/**------------------------------修改类服务--------------------------------*/
	
	/** 用户注册（注意事务）*/
	@PasswordEncode
	@Transactional
	public void save(User user) throws MailException {
		
		if(isExistByUsername(user.getUsername())) throw new RuntimeException("账号已经存在");
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
			sendActiviMail(user);	
		} catch (Exception e) {
			e.printStackTrace();
			throw new MailException(e.getMessage());
		}
		return;
	}
	
	@Transactional
	@PasswordEncode
	public void modify(User u) {
		dao.update(u, u.getUserId());
	}
	
	@Transactional
	public void deleteById(Long id) {
		dao.remove(User.class, id);
	}
	
	/**-----------------------------邮箱类服务------------------------------*/
	
	/**-------用户激活服务 ------------*/
	
	/** 发送激活邮件 */
	public void sendActiviMail(User user) throws MailException, MessagingException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("url", getActiUrl(user.getUserId()));
		mailService.sendMail(new MailContent("用户注册", "/mail/register"), user.getEmail(), map);
		return;
	}
	
	/**	生成激活用的URL */
	public String getActiUrl(Long userId) {
		String encodeStr = encrypt(userId, KEY[0]);
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
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("url", getRestUrl(user.getUserId()));
		mailService.sendMail(new MailContent("密码重置", "/mail/resetpwd"), user.getEmail(), map);
		return;
	}
	
	/** 生成重置链接 */
	private String getRestUrl(Long userId) {
		String encodeStr = encrypt(userId, KEY[1]);
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
	private String encrypt(Long userId, String[] key) {
		String encodes[] = {userId.toString(), String.valueOf(System.currentTimeMillis()), key[1]};
		return cipher.encode(key[0], encodes);
	}
}
