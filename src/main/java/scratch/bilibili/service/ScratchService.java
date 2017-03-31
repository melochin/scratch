package scratch.bilibili.service;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import scratch.bilibili.dao.VideoScratchRecordDao;
import scratch.bilibili.dao.VideoTypeDao;
import scratch.bilibili.model.Video;
import scratch.bilibili.model.VideoScratchRecord;
import scratch.bilibili.model.VideoType;

@Service("scratchBilibiliService")
public class ScratchService  {

	private ExecutorService exec;
	
	private static String VIDEO_API = "http://api.bilibili.com/archive_rank/getarchiverankbypartion?"
			+ "callback=callback&type=jsonp"; //&tid=82&pn=7000
	
	private static int VIDEO_PAGE_SIZE = 1;
	
	@Autowired
	private VideoTypeDao typeDao;
	
	@Autowired
	private VideoScratchRecordDao recordDao;
	
	@Autowired
	private SaveRunnable saveRunnable;
	
	@Autowired
	private VideoPageReader pageReader;
	
	private boolean start = false;
	
//	@Scheduled(cron="50/60 * *  * * ? ")
	public void scratchVideo() {
		if(isRun()) return;
		exec = Executors.newFixedThreadPool(1000);
		start = true;
		//获取视频分类
		List<VideoType> types = typeDao.list(2);
		if(types == null || types.size() == 0) {
			return;
		}
		try{
			//生成每种类别需要抓取的链接
			Map<VideoType, BlockingQueue<String>> typeMap = new HashMap<>();
			for(VideoType t:types) {
				//先读取第一页的URL，获取该类别视频的页数和总个数
				List<Long> list = pageReader.read(VIDEO_API + "&tid=" + t.getCode() + "&pn=1");
				if(list.size() == 0) continue;
				long count = list.get(0);
				long pages = list.get(1);
				//更新Type
				t.setVideoCount(count);
				typeDao.update(t);
				//决定最终抓取的页数
				pages = pages > VIDEO_PAGE_SIZE ? VIDEO_PAGE_SIZE : pages;
				BlockingQueue<String> urls = new LinkedBlockingQueue<String>();
				for(int p=1; p<=pages; p++) {
					urls.add(VIDEO_API + "&tid=" + t.getCode() + "&pn=" + p);
				}
				typeMap.put(t, urls);
			}
			//准备Record数据
			VideoScratchRecord record = new VideoScratchRecord();
			record.setStartTime(new Date());
			//---------------------------开启线程，执行爬虫任务------------------------------------
			//根据视频分类数量，来决定同时运行的线程数
			//barrier，多余的1是用来阻塞数据保存线程的，等待数据抓取完毕后，再进行数据保存
			BlockingQueue<Video> videos =  new LinkedBlockingQueue<Video>();
			CyclicBarrier barrier = new CyclicBarrier(typeMap.size() + 1);
			for(BlockingQueue<String> urls:typeMap.values()) {
				exec.execute(new ScratchVideo(urls, videos, barrier));
			}
			exec.execute(saveRunnable.setVideos(videos, record, barrier));
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			exec.shutdown();
			start = false;
		}

	}
	
	/**
	 * 定时任务是在线程中开启的，但是不允许该服务同时开启多个定时服务，必须设置同步锁
	 * @return
	 */
	public synchronized boolean isRun() {
		if(exec == null) return false;
		if(exec.isShutdown()) {
			if(exec.isTerminated()) {
				return false;
			} else {
				return true;
			}
		} else {
			return start;
		}
	}
	
	public VideoScratchRecord getRecentRecord() {
		return recordDao.getRecent();
	}

	public static void main(String[] args) throws MalformedURLException {
		ScratchService s = new ScratchService();
		s.scratchVideo();
	}

}

class ScratchVideo extends ScratchRunnable{

	private BlockingQueue<String> urls;
	
	private BlockingQueue<Video> videos;
	
	private VideoReader reader;
	
	private CyclicBarrier barrier;
	
	public ScratchVideo(BlockingQueue<String> urls, BlockingQueue<Video> videos,
			CyclicBarrier barrier) {
		this.urls = urls;
		this.videos = videos;
		this.barrier = barrier;
		this.reader = new VideoReader();
	}

	@Override
	public void scratch() {
		try{
			while(urls.size() > 0) {
				String url = urls.take();
				List<Video> list = reader.read(url);
				videos.addAll(list);
				log.debug(this + " 还剩余链接:" + urls.size());
			}
			log.debug(this + " 共抓取:" + videos.size());
		} catch (Exception e) {
			log.error(e.toString());
		} finally {
			try {
				//还好在finally里- -
				barrier.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				log.error("Barrier资源无法释放，部分线程将会挂起");
			}
		}
	}

}

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
					log.warn(this + " 发现异常，抓取数据失败");
					Thread.interrupted();
				}
				videos.addAll(vs);	
				log.debug(this + " 还剩余链接:" + sources.size());
			}
			if(size != videos.size()) {
				log.warn(this + "抓取video出错！目标数量:" + size + "，实际数量:" + videos.size());
			} else {
				log.debug(this + " 共抓取:" + videos.size());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
		}
	}
	
}
