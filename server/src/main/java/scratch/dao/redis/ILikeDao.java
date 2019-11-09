package scratch.dao.redis;

import java.util.Set;

public interface ILikeDao {

	/**
	 * 获得episode的点赞数量
	 * @param episodeId
	 * @return
	 */
	long getCount(long episodeId);

	/**
	 * 获取用户点赞的episode
	 * @param userId
	 * @return
	 */
	Set<Long> listEpisode(long userId);

	/**
	 * 增加episode点赞数量（+1)
	 * @param episodeId
	 * @return
	 */
	long increase(Long episodeId);

	/**
	 * 减少episode点赞数量(-1)
	 * @param episodeId
	 * @return
	 */
	long decrease(Long episodeId);

	/**
	 * 用户点赞的列表中加入episode
	 * @param userId
	 * @param episodeId
	 * @return
	 */
	boolean add(long userId, Long episodeId);

	/**
	 * 用户点赞的列表中移除episode
	 * @param userId
	 * @param episodeId
	 * @return
	 */
	boolean remove(long userId, Long episodeId);

}
