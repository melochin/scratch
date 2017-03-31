package scratch.bilibili.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scratch.bilibili.dao.VideoDao;
import scratch.bilibili.model.Video;
import scratch.service.Page;

@Service
public class VideoService {

	@Autowired
	private VideoDao videoDao;
	
	@Autowired
	private Page page;
	
	public List<Video> list(String keyword, Long type, String order, Integer pageNo){
		page.setCurPage(pageNo);
		return videoDao.list(keyword, type, order, page);
	}
	
}
