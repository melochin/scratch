package scratch.service;

import org.junit.Test;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import scratch.dao.UserDao;
import scratch.exception.AuthenException;
import scratch.model.entity.User;

import static org.mockito.ArgumentMatchers.any;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static scratch.context.RedisContext.USER_AUTH_MAIL;
import static scratch.context.RedisContext.USER_RESET_PSWD;
import static scratch.context.RedisContext.redisKey;

public class UserServiceMockTest {

	private UserService userService;

	private RedisTemplate redisTemplate;

	private ValueOperations valueOperations;

	private RedisOperations redisOperations;

	private DaoAuthenticationProvider authenticationProvider;

	private EmailService emailService;

	private UserDao userDao;

	public UserServiceMockTest() {
		this.redisTemplate = mock(RedisTemplate.class);
		this.authenticationProvider = mock(DaoAuthenticationProvider.class);
		this.emailService = mock(EmailService.class);
		this.userDao = mock(UserDao.class);

		when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
		this.valueOperations = redisTemplate.opsForValue();
		when(valueOperations.getOperations()).thenReturn(mock(RedisOperations.class));
		this.redisOperations = valueOperations.getOperations();

		this.userService = new UserService(redisTemplate, authenticationProvider
				, emailService, userDao);
	}

	@Test
	public void getById() throws Exception {
		// 测试数据
		Long userId = new Long(1);
		User user = new User(userId);
		// 设置mock行为
		when(userDao.getById(userId)).thenReturn(user);
		// 测试结果校验
		assertTrue(userService.getById(userId).equals(user));
		// 测试过程校验
		verify(userDao).getById(userId);
	}

	@Test
	public void getByName() throws Exception {
		// 测试数据
		String username = "test";
		User user = new User(username);
		// userDao.getByName(User user)　参数user对象由service产生
		// 不可能对传参进行模拟，因此使用any()
		when(userDao.getByName(any())).thenReturn(user);
		// 校验测试
		assertTrue(userService.getByName(username).equals(user));
		verify(userDao).getByName(any());
	}

	@Test
	public void getByNameAndEmail() throws Exception {
		// 模拟数据
		String username = "test";
		String email = "test@qq.com";
		User user = new User(new Long(1));
		// 模拟行为
		when(userDao.getByNameAndEmail(username, email)).thenReturn(user);
		// 校验测试
		assertTrue(userService.getByNameAndEmail(username, email).equals(user));
		verify(userDao).getByNameAndEmail(username, email);
	}

	@Test
	public void list() throws Exception {
	}

	@Test
	public void isExistByUsername() throws Exception {
	}

	@Test
	public void authen() throws Exception {
		String username = "test";
		String password = "test";
		User user = new User();
		user.setSalt(BCrypt.gensalt());
		boolean throwed = false;
		// 用户不存在时,抛出异常
		when(userDao.getByName(any())).thenReturn(null);
		try{
			userService.authen(username, password);
			throwed = false;
		} catch (AuthenException e) {
			throwed = true;
		}
		assertTrue(throwed);
		// 用户存在时，校验不通过，抛出异常
		when(userDao.getByName(any())).thenReturn(user);
		when(authenticationProvider.authenticate(any())).thenReturn(null);
		try{
			userService.authen(username, password);
			throwed = false;
		} catch (AuthenException e) {
			throwed = true;
		}
		assertTrue(throwed);
		// 用户存在时，校验通过
		when(authenticationProvider.authenticate(any()))
				.thenReturn(mock(Authentication.class));
		assertTrue(userService.authen(username, password) != null);
		// 其中两次测试进入了身份识别验证
		verify(authenticationProvider, times(2))
				.authenticate(any());
	}

	@Test
	public void save() throws Exception {
	}

	@Test
	public void modifyPassword() throws Exception {
	}

	@Test
	public void modify() throws Exception {
	}

	@Test
	public void deleteById() throws Exception {
	}

	@Test
	public void sendActiveMail() throws Exception {
	}

	@Test
	public void modifyEmail() throws Exception {
	}

	@Test
	public void getActiUrl() throws Exception {
		/*Long userId = new Long(1);
		String url = userService.getActiUrl(userId);
		System.out.println(url);*/
	}

	@Test
	public void confirmEmail() throws Exception {
		Long userId = new Long(1);
		String confirmCode = "code";
		when(valueOperations.get(redisKey(USER_AUTH_MAIL, userId)))
				.thenReturn(confirmCode);
		when(userDao.updateStatus(userId, "1")).thenReturn(1);
		//　校验通过：删除redis中的key
		assertTrue(userService.confirmEmail(userId, confirmCode) == 1);
		verify(redisOperations, times(1))
				.delete(redisKey(USER_AUTH_MAIL, userId));
		// 校验失败：传入的confrimCode与Redis中的不一致	不执行redis删除key
		assertTrue(userService.confirmEmail(userId, null) == -1);
		verify(redisOperations, times(0))
				.delete(any());
	}

	@Test
	public void sendRestMail() throws Exception {
	}

	@Test
	public void resetPassword() throws Exception {
		//userService.resetPassword()
	}

	@Test
	public void canReset() throws Exception {
		// 模拟数据
		Long userIdWithCode = new Long(1);
		Long userIdWitoutCode = new Long(2);
		String resetCode = "code";
		// 模拟行为
		when(valueOperations.get(redisKey(USER_RESET_PSWD, userIdWithCode)))
				.thenReturn(resetCode);
		when(valueOperations.get(redisKey(USER_RESET_PSWD, userIdWitoutCode)))
				.thenReturn(null);
		// 校验
		// resetCode 与　Redis中对应UserId存放的restCode相符
		assertTrue(userService.canReset(userIdWithCode, resetCode));
		// resetCode 为空时　或者 resetCode 与　Redis存放的不符时
		assertFalse(userService.canReset(userIdWithCode, null));
		assertFalse(userService.canReset(userIdWithCode, ""));
		assertFalse(userService.canReset(userIdWithCode, "test"));
		// 对应的UserId在Redis中不存resetCode时
		assertFalse(userService.canReset(userIdWitoutCode, resetCode));

	}

}