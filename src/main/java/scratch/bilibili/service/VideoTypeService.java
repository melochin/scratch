package scratch.bilibili.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import scratch.bilibili.dao.VideoDao;
import scratch.bilibili.dao.VideoTypeDao;

@Service
public class VideoTypeService {

	@Autowired
	private VideoDao videoDao;
	
	@Autowired
	private VideoTypeDao videoTypeDao;
	
	public Map listVideoCount() {
		Map<String, Object> map = new HashMap<String, Object>();
		videoDao.listCountByType();
		ObjectMapper m = new ObjectMapper();
		map.put("types", videoTypeDao.list(1));
		map.put("currentCount", videoDao.listCountByType());
		return map;
	}
	
}
