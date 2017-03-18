package scratch.test;

import org.junit.Test;
import org.omg.CORBA.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import scratch.dao.SearchInfoDao;
import scratch.model.User;
import scratch.service.UserSerivce;

public class UserServiceTest extends ContextClass{

	@Autowired
	private UserSerivce userSerivce;
	
	@Autowired
	private SearchInfoDao searchInfoDao;
	
	//”√ªß◊¢≤·≤‚ ‘
	@Rollback
	@Transactional
	public void registerTest() {
		User user = new User("hejianok7878", "hejianok");
		user.setEmail("398299262@qq.com");
		userSerivce.add(user);
	}
	
}
