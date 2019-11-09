package scratch.service.anime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scratch.dao.redis.ILikeDao;

import java.util.Set;

@Service
public class LikeService {

	@Autowired
	private ILikeDao likeDao;

	public long vote(long userId, Long episodeId) {
		if (likeDao.add(userId, episodeId) == false) return -1;
		return likeDao.increase(episodeId);
	}

	public long cancelVote(long userId, Long episodeId) {
		if (likeDao.remove(userId, episodeId) == false) return -1;
		return likeDao.decrease(episodeId);
	}

	public long getVotes(long episode) {
		return likeDao.getCount(episode);
	}

	public Set<Long> listEpisode(long userId) {
		return likeDao.listEpisode(userId);
	}

}
