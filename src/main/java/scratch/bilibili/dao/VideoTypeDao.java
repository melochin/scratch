package scratch.bilibili.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import scratch.bilibili.model.VideoType;
import scratch.dao.BasicDao;

@Repository
public class VideoTypeDao extends BasicDao<VideoType>{
	
	private final static String QUERY_LEVEL = "from VideoType where level = ?";
	
	public List<VideoType> list(Integer level) {
		List<VideoType> list = listByHql(QUERY_LEVEL, level);
		return list;
	}
	
}
