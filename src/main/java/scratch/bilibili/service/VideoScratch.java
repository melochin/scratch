package scratch.bilibili.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import scratch.bilibili.dao.VideoDao;
import scratch.bilibili.dao.VideoScratchRecordDao;
import scratch.bilibili.model.Video;
import scratch.bilibili.model.VideoScratchRecord;
import scratch.bilibili.model.VideoType;
import scratch.bilibili.service.reader.VideoReader;

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
	
	@Autowired
	private PlatformTransactionManager tm;
	
	private ExecutorService exec;
	
	private boolean start = false;
	
	public void run(Map<VideoType, List<String>> typeMap) {
		if(typeMap == null || typeMap.size() == 0) return;
		start = true;
		//初始化视频队列
		BlockingQueue<Video> videoQueue = new LinkedBlockingQueue<>();
		VideoScratchRecord record = new VideoScratchRecord();
		//初始化线程池
		CyclicBarrier barrier = new CyclicBarrier(typeMap.size() + 1);
		exec = Executors.newCachedThreadPool();
		//分配URL，开始执行抓取任务
		record.setStartTime(new Date());
		for(Entry<VideoType, List<String>> e : typeMap.entrySet()){
			BlockingQueue<String> urlQueue = new LinkedBlockingQueue<>(e.getValue());
			exec.execute(new VideoScratchTask(urlQueue, videoQueue, barrier));
		}
		exec.execute(new VideoSaveTask(videoQueue, record, barrier));
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

		private BlockingQueue<Video> videos;
		
		private CyclicBarrier barrier;
		
		public VideoScratchTask(BlockingQueue<String> urls, BlockingQueue<Video> videos,
				CyclicBarrier barrier) {
			this.urls = urls;
			this.videos = videos;
			this.barrier = barrier;
		}

		@Override
		public void scratch() {
			try{
				while(urls.size() > 0) {
					String url = urls.take();
					List<Video> list = reader.read(url);
					videos.addAll(list);
					log.debug(this + " ��ʣ������:" + urls.size());
				}
				log.debug(this + " ��ץȡ:" + videos.size());
			} catch (Exception e) {
				log.error(e.toString());
			} finally {
				try {
					//������finally��- -
					barrier.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					log.error("Barrier��Դ�޷��ͷţ������߳̽������");
				}
			}
		}

	}
	
	public class VideoSaveTask implements Runnable{
		
		private static final int COMMIT_SIZE = 200;
		
		private BlockingQueue<Video> videos;
		
		private CyclicBarrier barrier;

		private VideoScratchRecord record;
		
		public VideoSaveTask(BlockingQueue<Video> videos, VideoScratchRecord record,
				CyclicBarrier barrier) {
			this.videos = videos;
			this.barrier = barrier;
			this.record = record;
		}
		
		@Override
		public void run() {
			long saveCount = 0;
			boolean await = false;
			try {	
				while(barrier.getNumberWaiting() < barrier.getParties() - 1) {
					System.out.println("number:" + barrier.getNumberWaiting());
					System.out.println("parties:" + barrier.getParties());
					if(videos.size() > 0) {
						saveCount = saveCount + saveVideos();
						System.out.println("count " + saveCount);
					}
				}
				barrier.await();
				await = true;
				while(videos.size() > 0) {
					saveCount = saveCount + saveVideos();
					System.out.println("count " + saveCount);
				}
			} catch (Exception e) {
				record.setError(record.getError() + e.toString());	//���������Ϣ
				if(!await) {
					try {
						barrier.await();
					} catch (InterruptedException | BrokenBarrierException e1) {
						e1.printStackTrace();
					}
				}
			} finally {
				record.setEndTime(new Date());	//�������ʱ��
				record.setScratchCount(saveCount);
				recordDao.save(record);
			}
		}
		
		public int saveVideos() throws Exception {
			DefaultTransactionDefinition td = new DefaultTransactionDefinition();
			td.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			TransactionStatus ts = tm.getTransaction(td);
			int i = 0;
			try{
				while(i < COMMIT_SIZE && videos.size() > 0) {
					Video v = videos.take();
					videoDao.saveOrUpdate(v, v.getAvid());
					i++;
				}
				tm.commit(ts);
			} catch (Exception e) {
				if(!ts.isCompleted()) {
					tm.rollback(ts);
				}
				e.printStackTrace();
			} 
			return i;
		}

	}
	
}
