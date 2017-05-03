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
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import scratch.aspect.PasswordEncode;
import scratch.controller.RegisterController;
import scratch.controller.UserController;
import scratch.dao.UserDao;
import scratch.exception.MailException;
import scratch.model.User;
import scratch.support.cipher.CipherSupport;
import scratch.support.service.MailService;
import scratch.support.service.PageBean;

@Transactional
@Service
public class UserService {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	private static Logger log = Logger.getLogger(UserService.class);
	
	private static long TIME_OUT = 10;	//有效期10分钟
	
	private static String[][] KEY = new String[][] {{"51748915", "87741296"}, 
		{"12744597", "26741096"}};
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private CipherSupport cipher;
	
	@Autowired
	private UserDao dao;
	
	/**
	 * 用户校验(登录)
	 * @param user
	 * @return
	 */
	@PasswordEncode
	public User verify(User user) {
		return dao.getByNameAndPass(user);
	}
	
	/**
	 * 用户注册
	 * 需要注意事务
	 * @param user
	 * @throws MailException 
	 */
	@PasswordEncode
	public void add(User user) throws MailException {
		if(isExist(user)) throw new RuntimeException("账号已经存在");
		dao.save(user);
		//如果用户的状态处于激活，那就不发生邮件
		//后台直接添加的用户可能处于该状态
		if(!StringUtils.isEmpty(user.getStatus()) && user.getStatus().equals("1")) return;
		//抛出checked异常，不影响事务
		try{
			sendActiviMail(user);	
		} catch (Exception e) {
			throw new MailException();
		}
		return;
	}
	
	public User get(String username, String email) {
		return dao.getByNameAndEmail(username, email);
	}

	@PasswordEncode
	public void update(User u) {
		dao.update(u, u.getUserId());
	}
	
	
	/**
	 * 判断用户名是否被人使用
	 * @param user
	 * @return
	 */
	public boolean isExist(User user) {
		return (dao.getByName(user) != null);
	}
	
	/**
	 * 通过用户id获取用户
	 * @param userId
	 * @return
	 */
	public User getById(long userId) {
		return dao.getById(userId);
	}
	
	public boolean activi(String actiCode) {
		//根据解密激活码，获取信息
		String[] infos = decodeActi(actiCode);
		if(infos == null) {
			return false;
		}
		String username = infos[0];
		long registerTime = Long.valueOf(infos[1]);
		long curTime = System.currentTimeMillis();
		//判断激活码是否过期
		if(registerTime + TIME_OUT < curTime) {
			return false;
		}
		//激活用户状态
		return dao.updateStatus(username, "1") == 1;
	}
	
	public void sendActiviMail(User user) throws MailException, MessagingException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("url", getActiUrl(user.getUserId()));
		mailService.sendMail(MailTemplate.REGISTER, user.getEmail(), map);
		return;
	}
	
	public void sendRestMail(User user) throws MailException, MessagingException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("url", getRestUrl(user.getUserId()));
		mailService.sendMail(MailTemplate.RESETPWD, user.getEmail(), map);
		return;
	}
	
	
	/**
	 * 根据激活码获取用户名和注册时间
	 * array[0]:用户名
	 * array[1]:注册时间
	 * @param actiCode
	 * @return
	 */
	private String[] decodeActi(String actiCode) {
		log.debug("激活码：" + actiCode);
		String info = cipher.decode(actiCode);
		if(info == null) {
			return null;
		}
		log.debug("解密后的激活码信息：" + info);
		String[] infos = info.split("&");
		if(infos.length != 2) {
			return null;
		}
		return infos;
	}
	
	public boolean decodeReset(String encode, Long userId) {
		Long decodeUserId = decrypt(encode, KEY[1]);
		return decodeUserId != null ? decodeUserId.equals(userId) : false; 
	}
	
	/**
	 * 
	 * 根据用户名生成激活用的URL
	 * @param username
	 * @return
	 */
	private String getActiUrl(Long userId) {
		String encodeStr = encrypt(userId, KEY[0]);
		//将加密后的字符串放入缓存
		redisTemplate.opsForValue()
			.set(encodeStr, userId.toString(), TIME_OUT, TimeUnit.MINUTES);
		//生成账号激活的URL
		String url = MvcUriComponentsBuilder.fromMethodName(
				RegisterController.class, "activiti", encodeStr, null)
				.build()
				.encode()
				.toUriString();
		return url;
	}
	
	private String getRestUrl(Long userId) {
		String encodeStr = encrypt(userId, KEY[1]);
		//将加密后的字符串放入缓存
		redisTemplate.opsForValue()
			.set(encodeStr, userId.toString(), TIME_OUT, TimeUnit.MINUTES);
		//生成密码重置的URL
		String url = MvcUriComponentsBuilder.fromMethodName(
				UserController.class, "resetPasswordForm", encodeStr, userId, null)
				.build()
				.encode()
				.toUriString();
		return url;		
	}
	
	/**
	 * 加密方式：md5(userId & currentTimeMills & key2, key1)
	 * @param userId
	 * @param key
	 * @return
	 */
	private String encrypt(Long userId, String[] key) {
		String encode = userId + "&" + System.currentTimeMillis() + "&" + key[1];
		return cipher.encode(key[0], encode);
	}
	
	private Long decrypt(String input, String[] key) {
		String decode = cipher.decode(key[0], input);
		//解析数据个数核对
		String[] infos = (decode == null ? null : decode.split("&"));
		if(infos == null || infos.length != 3) {
			return null;
		}
		//KEY2核对
		if(!key[1].equals(infos[2])) {
			return null;
		}
		//用户有效性核对
		Long userId = Long.valueOf(infos[0]);
		if(userId == null || userId == 0) {
			return null;
		}
		return userId;
	}
	
	public List<User> list() {
		List<User> userList = dao.list(User.class);
		return userList;
	}
	
	public PageBean<User> findAll(int page, int pageSize) {
		return dao.findAll(page, pageSize);
	}
	
	
	public User getById(Long id) {
		return dao.getById(id);
	}
	
	public void deleteById(Long id) {
		dao.remove(User.class, id);
	}
	
}
