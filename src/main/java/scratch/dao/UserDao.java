package scratch.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import scratch.dao.inter.IUserDao;
import scratch.model.entity.User;
import scratch.support.service.Page;
import scratch.support.service.PageBean;

@Repository
public class UserDao extends BasicDao<User> {

	private static final String GET_USER_BY_NAME = "from User where username = ?";
	private static final String GET_USER_BY_ID = "from User where userId = ?";
	private static final String GET_USER_BY_NAME_AND_PASS = "from User "
			+ "where username = ? and password = ?";
	private static final String UPDATE_USER_STATUS_BY_NAME = "update User set status = ? "
			+ "where username = ?";
	private static final String UPDATE_USER_STATUS_BY_ID = "update User set status = ? "
			+ "where userId = ?";
	private static final String GET_USER_BY_NAME_AND_EMAIL = "from User "
			+ "where username = ? and email = ?";
	private static final String COUNT_BY_USERNAME = "select count(*) from User where username = ?";

	public User getByName(String username) {
		return getByHql(GET_USER_BY_NAME, username);
	}

	public User getById(long userId) {
		return getByHql(GET_USER_BY_ID, userId);
	}

	public User getByNameAndPwd(String username, String password) {
		return getByHql(GET_USER_BY_NAME_AND_PASS, username, password);
	}
	
	public Long countByName(String username) {
		return getByHql(COUNT_BY_USERNAME, username);
	}

	public int updateStatus(String username, String status) {
		return updateByHql(UPDATE_USER_STATUS_BY_NAME, status, username);
	}
	
	public int updateStatus(Long userId, String status) {
		return updateByHql(UPDATE_USER_STATUS_BY_ID, status, userId);
	}

	public User getByNameAndEmail(String username, String email) {
		return getByHql(GET_USER_BY_NAME_AND_EMAIL, username, email);
	}

	public PageBean<User> findAll(int page, int pageSize) {
		Page p = new Page(page, pageSize);
		Criteria c = createCriteria(User.class);
		List<User> userList = listByCriteria(c, p);
		return new PageBean<User>(userList, p);
	}

}
