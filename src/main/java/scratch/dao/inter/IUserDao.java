package scratch.dao.inter;

import java.util.List;

import scratch.model.entity.User;
import scratch.support.service.PageBean;

public interface IUserDao {
	
	List<User> findAll();
	
	PageBean<User> findAll(int page, int pageSize);
	
}
