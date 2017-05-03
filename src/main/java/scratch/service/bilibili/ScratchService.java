package scratch.service.bilibili;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scratch.dao.VideoScratchRecordDao;
import scratch.dao.VideoTypeDao;
import scratch.model.VideoScratchRecord;
import scratch.model.VideoType;
import scratch.service.reader.VideoPageReader;

@Service("scratchBilibiliService")
public class ScratchService  {

	private static String VIDEO_API = "http://api.bilibili.com/archive_rank/getarchiverankbypartion?"
			+ "callback=callback&type=jsonp"; //&tid=82&pn=7000
	
	private static int VIDEO_PAGE_SIZE = 1000;
	
	@Autowired
	private VideoTypeDao typeDao;
	
	@Autowired
	private VideoScratchRecordDao recordDao;
	
	@Autowired
	private VideoPageReader pageReader;
	
	@Autowired
	private VideoScratch videoScratch;
	
	//@Scheduled(cron="50/60 * *  * * ? ")
	public void scratchVideo() {
		if(isRun()) return;
		//读取所有类别
		List<VideoType> types = typeDao.list(2);
		if(types == null || types.size() == 0) return;
		//生成URL
		Map<VideoType, List<String>> typeMap = new HashMap<>();
		for(VideoType t:types) {
			List<Long> list = pageReader.read(VIDEO_API + "&tid=" + t.getCode() + "&pn=1");
			if(list.size() == 0) continue;
			long count = list.get(0);
			long pages = list.get(1);
			//更新视频总数
			t.setVideoCount(count);
			typeDao.update(t);
			//获取总页数
			pages = pages > VIDEO_PAGE_SIZE ? VIDEO_PAGE_SIZE : pages;
			List<String> urls = new ArrayList<String>();
			for(int p=1; p<=pages; p++) {
				urls.add(VIDEO_API + "&tid=" + t.getCode() + "&pn=" + p);
			}
			typeMap.put(t, urls);
		}
		//抓取数据
		videoScratch.run(typeMap);
	}
	
	/**
	 * ��ʱ���������߳��п����ģ����ǲ�����÷���ͬʱ���������ʱ���񣬱�������ͬ����
	 * @return
	 */
	public boolean isRun() {
		return videoScratch.isRun();
	}
	
	public VideoScratchRecord getRecentRecord() {
		return recordDao.getRecent();
	}

}

/*

class ScratchVideoSpecifi extends ScratchRunnable {

	private BlockingQueue<Video> sources;
	
	private BlockingQueue<Video> videos;
	
	private CyclicBarrier barrier;
	
	private VideoSpecificReader reader;
	
	private static final String VIDEO_URL = "http://api.bilibili.com/view?";
	
	private static final String APPKEY = "8e9fc618fbd41e28";
	
	public ScratchVideoSpecifi(BlockingQueue<Video> sources, 
			BlockingQueue<Video> videos, CyclicBarrier barrier) {
		this.sources = sources;
		this.videos = videos;
		this.barrier = barrier;
		this.reader = new VideoSpecificReader();
	}
	
	@Override
	public void scratch() {
		try {
			barrier.await();
			int size = sources.size();
			while(sources.size() > 0) {
				Video source = sources.take();
				if(source.getAvid() == null) {
					continue;
				}
				TimeUnit.MILLISECONDS.sleep(1500);
				String url = VIDEO_URL + "id=" + source.getAvid() + "&appkey=" + APPKEY;
				List<Video> vs = reader.read(url);
				if(vs == null) {
					log.warn(this + " �����쳣��ץȡ����ʧ��");
					Thread.interrupted();
				}
				videos.addAll(vs);	
				log.debug(this + " ��ʣ������:" + sources.size());
			}
			if(size != videos.size()) {
				log.warn(this + "ץȡvideo����Ŀ������:" + size + "��ʵ������:" + videos.size());
			} else {
				log.debug(this + " ��ץȡ:" + videos.size());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
		}
	}
	
}
*/