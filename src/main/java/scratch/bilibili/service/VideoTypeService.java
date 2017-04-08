package scratch.bilibili.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scratch.bilibili.dao.VideoDao;
import scratch.bilibili.dao.VideoTypeDao;
import scratch.bilibili.model.VideoType;

@Service
public class VideoTypeService {

	@Autowired
	private VideoDao videoDao;
	
	@Autowired
	private VideoTypeDao videoTypeDao;
	
	@SuppressWarnings("rawtypes")
	public Map listVideoCount() {
		Map<String, Object> map = new HashMap<String, Object>();
		videoDao.listCountByType();
		map.put("types", videoTypeDao.list(1));
		map.put("currentCount", videoDao.listCountByType());
		return map;
	}
	
	public List<VideoType> list(Integer level) {
		return videoTypeDao.list(level);
	}
	
}
