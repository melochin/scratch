package scratch.service.test;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import scratch.model.User;
import scratch.service.UserService;
import scratch.support.cipher.CipherSupport;
import scratch.support.service.MailException;
import scratch.support.service.PageBean;
import scratch.test.ContextTest;

public class UserServiceTest extends ContextTest{

	@Autowired
	private UserService userService;
	
	@Autowired
	private CipherSupport cipher;
	
/*	@Test
	public void resetPassword() {
		List<User> user = userService.list();
		user.forEach(u -> {
			if(!StringUtils.isEmpty(u.getSalt())) {
				return;
			}
			// 解密
			String password = cipher.decode(u.getHashedPassword());
			u.setPassword(password);
			userService.modify(u);
		});
	}*/
	
	
	/** 用户查询相关服务测试  */
	@Test
	public void findUserTest() {
		// 所有用户信息查询
		List<User> list = userService.list();
		assertTrue("list fail", list.size() > 0);
		
		// 用户信息分页查询
		int pageSize = 5;
		int page = 1;
		PageBean<User> pageUser = userService.list(page, pageSize);
		assertTrue("list page fial", pageUser.getData().size() == pageSize);
		
		// 用户名重复性查询
		boolean isExist = userService.isExistByUsername("consequent");
		assertTrue("isExistByUsername fail", isExist == true);
		
		isExist = userService.isExistByUsername("*******");
		assertTrue("isExistByUsername fail", isExist == false);
		
		// 用户账号密码校验
		User user = userService.getByNameAndPwd("consequent", "hejianok");
		assertTrue("getByNameAndPwd fail", user != null);
		
		user = userService.getByNameAndEmail("consequent", "398299262@qq.com");
		assertTrue("getByNameAndEmail fail", user != null);
	}
	
	@Rollback
	@Transactional
	@Test
	public void saveUserTest() {
		User user = new User();
		String username = "test999";
		String password = "test999";
		user.setUsername(username);
		user.setPassword(password);
		user.setStatus("1");
		
		// 用户保存
		try {
			userService.save(user);
		} catch (MailException e) {
			e.printStackTrace();
		}
		User dbUser = userService.getByNameAndPwd(username, password);
		assertTrue("save fail", dbUser != null);
		
		// 用户密码加密
		String encodePassword = cipher.encode(password);
		assertTrue("password encode fail: " + user.getPassword() + " != " + encodePassword, 
				user.getPassword().equals(encodePassword));
	}
	
	@Rollback
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Test
	public void updateUserTest() {
		User user = createTestUser();
		String newPassword = "test1000";
		// 保存用户，用于更新测试
		try {
			userService.save(user);
		} catch (MailException e) {
			e.printStackTrace();
		}
		// 更新用户密码
		user.setPassword(newPassword);
		userService.modify(user);
		// 用新密码查找用户
		User dbUser = userService.getByNameAndPwd(user.getUsername(), newPassword);
		assertTrue("update user password fail", dbUser != null);
	}
	
	@Rollback
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Test
	public void deleteUserTest() {
		User user = createTestUser();
		try {
			userService.save(user);
		} catch (MailException e) {
			e.printStackTrace();
		}
		// 删除用户
		User dbUser = userService.getById(user.getUserId());
		assertTrue("delete user fail", dbUser != null);
		userService.deleteById(user.getUserId());
		dbUser = userService.getById(user.getUserId());
		assertTrue("delete user fail", dbUser == null);
	}
	
	private User createTestUser() {
		User user = new User();
		String username = "test999";
		String password = "test999";
		user.setUsername(username);
		user.setPassword(password);
		user.setStatus("1");
		return user;
	}
	
	@Rollback
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Test
	public void activitTest() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException {
		Long userId = new Long(59);
		String url = userService.getActiUrl(userId);
		System.out.println(url);
		String words[] = url.split("/");
		String activiCode = words[words.length-1];
		TimeUnit.SECONDS.sleep(10);
		assertTrue(userService.activi(userId, activiCode) == 1);
	}
	
	
	
	/**
	 * �û�ע�����
	 * 1.���������Ƿ�ᵼ���û�ע��ع���
	 * 2.���û�ע��������checked�쳣��ʹ�ʼ�����ʧ�ܣ�Ҳ���ᵼ��ע���û�����ع�
	 * @throws MailException 
	 */
	@Rollback
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	//@Test
	public void registerTest() throws MailException {
		User user = new User("hejianok11", "hejianok");
		user.setEmail("398299262@qq.com");
	//	userService.add(user);
	}
	
	//@Test
	public void sendResetMailTest() throws MailException, MessagingException {
		User user = new User("consequent", "consequqent");
		user.setEmail("398299262@qq.com");
		user.setUserId(new Long(111));
		userService.sendRestMail(user);
	}
	
}
