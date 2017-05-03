package scratch.service.bilibili;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import scratch.dao.VideoDao;
import scratch.dao.VideoScratchRecordDao;
import scratch.model.Video;
import scratch.model.VideoScratchRecord;
import scratch.model.VideoType;
import scratch.service.reader.VideoReader;

/**
 * 视频的抓取类
 * 1.视频抓取
 * 2.视频保存
 * @author melochin
 *
 */
@Service
public class VideoScratch {
	
	@Autowired
	private VideoReader reader;

	@Autowired
	private VideoDao videoDao;
	
	@Autowired
	private VideoScratchRecordDao recordDao;
	
	private Logger log = Logger.getLogger(VideoScratch.class);
	
	private ExecutorService exec;
	
	private VideoScratchRecord record;
	
	private boolean start = false;
	
	public void run(Map<VideoType, List<String>> typeMap) {
		if(typeMap == null || typeMap.size() == 0) return;
		start = true;
		record = new VideoScratchRecord();
		//初始化线程池
		exec = Executors.newCachedThreadPool();
		CyclicBarrier barrier = new CyclicBarrier(typeMap.size() * 2, new Runnable() {
			
			@Override
			public void run() {
				record.setEndTime(new Date());
				recordDao.save(record);
			}
			
		});
		//分配URL，开始执行抓取任务
		record.setStartTime(new Date());
		for(Entry<VideoType, List<String>> e : typeMap.entrySet()){
			BlockingQueue<String> urlQueue = new LinkedBlockingQueue<>(e.getValue());
			Exchanger<List<Video>> exchanger = new Exchanger<List<Video>>();
			exec.execute(new VideoScratchTask(urlQueue, exchanger, barrier));
			exec.execute(new VideoSaveTask(record, exchanger, barrier));
		}
		exec.shutdown();
		start = false;
	}
	
	public boolean isRun() {
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
	
	class VideoScratchTask extends ScratchRunnable{
		
		private BlockingQueue<String> urls;

		private List<Video> videos;
		
		private Exchanger<List<Video>> exchanger;
		
		private CyclicBarrier barrier;
		
		public VideoScratchTask(BlockingQueue<String> urls, 
				Exchanger<List<Video>> exchanger, CyclicBarrier barrier) {
			this.urls = urls;
			this.videos = new ArrayList<Video>();
			this.exchanger = exchanger;
			this.barrier = barrier;
		}

		@Override
		public void scratch() {
			try{
				while(urls.size() > 0) {
					String url = urls.take();
					videos = reader.read(url);
					log.debug(this + " 本次抓取数据：" + videos.size() + " 剩余URL：" + urls.size());
					videos = exchanger.exchange(videos);
					log.debug(this + " 发起数据交，交换后数据：" + videos.size());
				}
				log.debug(this + " 完成任务，共抓取数据：" + videos.size());
			} catch (Exception e) {
				log.error(e.toString());
			} finally {
				try {
					log.debug(this + " barrier累计：" + barrier.getNumberWaiting());
					barrier.await();
				} catch (InterruptedException | BrokenBarrierException e) {
				}
			}
		}

	}
	
	public class VideoSaveTask implements Runnable{
		
		private List<Video> videos;
		
		private VideoScratchRecord record;
		
		private Exchanger<List<Video>> exchanger;
		
		private CyclicBarrier barrier;
		
		public VideoSaveTask(VideoScratchRecord record,
				Exchanger<List<Video>> exchanger, CyclicBarrier barrier) {
			this.videos = new ArrayList<Video>();
			this.barrier = barrier;
			this.exchanger = exchanger;
			this.record = record;
		}
		
		@Override
		public void run() {
			long saveCount = 0;
			try {
				while(!Thread.interrupted()) {
					System.out.println(Thread.currentThread().getName() + " 交换前数据：" + videos.size());
					videos = exchanger.exchange(videos, 15, TimeUnit.SECONDS);
					System.out.println(this + " 发起数据交交换，交换后数据：" + videos.size());
					try {
						videoDao.saveOrUpdateList(videos);
						saveCount += videos.size();
						System.out.println(this + " 当前保存数据量" + saveCount);
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						videos.removeAll(videos);
					}
				}
			} catch (InterruptedException e) {
				log.error(e);
			} catch (TimeoutException e1) {
				log.error(e1);
				System.out.println("长时间未获取数据任务终止");
			} finally {
				synchronized (record) {
					Long scratchCount = record.getScratchCount();
					if(scratchCount == null) {
						scratchCount = new Long(0);
					}
					record.setScratchCount(scratchCount + saveCount);	
				}
				try {
					System.out.println(this + " barrier累计：" + barrier.getNumberWaiting());
					barrier.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					log.error(e);
				}
			}
		}

	}
	
}
