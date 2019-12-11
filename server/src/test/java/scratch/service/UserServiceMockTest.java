package scratch.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import scratch.dao.inter.IUserDao;
import scratch.exception.AuthenException;
import scratch.model.entity.User;

import static org.mockito.ArgumentMatchers.any;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static scratch.model.RedisKey.USER_AUTH_MAIL;

public class UserServiceMockTest {

	private UserService userService;

	@Mock
	private RedisService redisService;

	@Mock
	private AuthenticationProvider authenticationProvider;

	@Mock
	private EmailProducer emailProducer;

	@Mock
	private IUserDao userDao;

	private static final String TEST_USERNAME = "test";

	private static final long TEST_USERID = 1;

	private User user;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		userService = new UserService(redisService, emailProducer, userDao);

		user = new User(TEST_USERID);
		user.setUsername(TEST_USERNAME);
		user.setPassword("test");
		user.setSalt(BCrypt.gensalt());
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

	@Test(expected = AuthenException.class)
	public void authenInvalidUser() {
		when(userDao.getByName(user.getUsername()))
				.thenReturn(user);
		userService.authen(null, user.getPassword());
	}

	@Test(expected = AuthenException.class)
	public void authenFail() {
		when(userDao.getByName(user.getUsername()))
				.thenReturn(user);
		when(authenticationProvider.authenticate(any())).thenReturn(null);
		userService.authen(user.getUsername(), user.getPassword());
	}

	@Test
	public void authenSucess() {
		when(userDao.getByName(user.getUsername()))
				.thenReturn(user);
		when(authenticationProvider.authenticate(any())).thenReturn(mock(Authentication.class));
		assertTrue(userService.authen(user.getUsername(), user.getPassword()) != null);
		verify(authenticationProvider, times(1))
				.authenticate(any());
	}


/*	@Test
	public void authen() throws Exception {
		String username = "test";
		String password = "test";
		User user = new User();
		user.setSalt(BCrypt.gensalt());
		boolean throwed = false;
		// 用户不存在时,抛出异常
		when(userDao.getByName(notNull())).thenReturn(user);

		try{
			userService.authen(username, password);
			throwed = false;
		} catch (AuthenException e) {
			throwed = true;
		}
		assertTrue(throwed);
		// 用户存在时，校验不通过，抛出异常
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
	}*/

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
	public void confirmEmailWithRightCode() throws Exception {
		Long userId = new Long(1);
		String confirmCode = "code";
		String redisKey = "key";
		//　校验通过：删除redis中的key
		when(redisService.getKey(USER_AUTH_MAIL, String.valueOf(userId)))
				.thenReturn(redisKey);
		when(redisService.equalsTo(redisKey, confirmCode)).thenReturn(true);
		when(userDao.updateStatus(userId, "1")).thenReturn(1);
		assertTrue(userService.confirmEmail(userId, confirmCode) == 1);
		verify(redisService, times(1)).delete(redisKey);
	}

	@Test
	public void confirmEmailWithWrongCode() {
		// 校验失败：传入的confrimCode与Redis中的不一致	不执行redis删除key
		when(redisService.equalsTo(any(), any())).thenReturn(false);
		assertTrue(userService.confirmEmail(new Long(1), "test") == -1);
		verify(redisService, times(0)).delete(any());
	}


	@Test
	public void sendRestMail() throws Exception {
	}

	@Test
	public void resetPassword() throws Exception {
		//userService.resetPassword()
	}

/*	@Test
	public void canReset() throws Exception {
		// 模拟数据
		Long userIdWithCode = new Long(1);
		Long userIdWitoutCode = new Long(2);
		String resetCode = "code";
		// 模拟行为
		when(redisService.get(redisKey(USER_RESET_PSWD, userIdWithCode)))
				.thenReturn(resetCode);
		when(redisService.get(redisKey(USER_RESET_PSWD, userIdWitoutCode)))
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

	}*/

}