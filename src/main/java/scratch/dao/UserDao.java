package scratch.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.springframework.stereotype.Repository;

import scratch.dao.inter.IUserDao;
import scratch.model.User;
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
	private static final String GET_USER_BY_NAME_AND_EMAIL = "from User "
			+ "where username = ? and email = ?";

	public User getByName(User user) {
		return getByHql(GET_USER_BY_NAME, user.getUsername());
	}

	public User getById(long userId) {
		return getByHql(GET_USER_BY_ID, userId);
	}

	public User getByNameAndPass(User user) {
		return getByHql(GET_USER_BY_NAME_AND_PASS, user.getUsername(), user.getPassword());
	}

	public int updateStatus(String username, String status) {
		return updateByHql(UPDATE_USER_STATUS_BY_NAME, status, username);
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
