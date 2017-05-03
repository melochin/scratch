package scratch.service.bilibili;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scratch.dao.VideoDao;
import scratch.dao.VideoTypeDao;
import scratch.dao.inter.IVideoTypeDao;
import scratch.model.VideoType;

@Service
public class VideoTypeService {

	@Autowired
	private VideoDao videoDao;
	
	@Autowired
	private VideoTypeDao videoTypeDao;
	
	@Autowired
	private IVideoTypeDao typeDao;
	
	
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
	
	public List<VideoType> list() {
		Map<Integer, VideoType> typeMap = new HashMap<Integer, VideoType>();
		List<VideoType> result = new ArrayList<VideoType>();
		List<VideoType> types = typeDao.list();
		//整理父子关系
		//筛选1级类别
		for(VideoType type:types) {
			if(type.getLevel() == 1) {
				typeMap.put(type.getCode(), type);
			}
		}
		//处理2级类别
		for(VideoType type:types) {
			if(type.getLevel() == 2) {
				Integer parentCode = type.getParentType().getCode();
				VideoType parent = typeMap.get(parentCode);
				parent.addChildType(type);
			}
		}
		result.addAll(typeMap.values());
		return result;
	}

	public void modify(VideoType type) {
		typeDao.modify(type);
	}

	public void delete(Integer code) {
		typeDao.delete(code);
	}

	public void insert(VideoType type) {
		typeDao.save(type);
	}
	
}
