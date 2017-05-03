package scratch.dao.inter;

import scratch.model.User;
import scratch.support.service.PageBean;

public interface IUserDao {
	
	PageBean<User> findAll(int page, int pageSize);
	
}
