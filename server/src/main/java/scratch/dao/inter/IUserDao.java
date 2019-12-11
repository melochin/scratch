package scratch.dao.inter;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import scratch.model.entity.User;
import scratch.support.service.PageBean;

public interface IUserDao {
	
	List<User> findAll();

	/**
	 * 返回的User包含加密的密码
	 * @param userId
	 * @return
	 */
	User getById(long userId);

	User getByName(String username);

	User getByNameAndEmail(@Param("username") String username,
						   @Param("email") String email);

	int countByName(String username);

	long save(User user);

	int updateStatus(@Param("userId") Long userId,
					 @Param("status") String status);

	int updatePassword(@Param("userId") Long userId,
					   @Param("password") String password,
					   @Param("salt") String salt);

	int delete(long userId);
}
