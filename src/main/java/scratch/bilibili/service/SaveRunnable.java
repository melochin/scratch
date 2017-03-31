package scratch.bilibili.service;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import scratch.bilibili.dao.VideoDao;
import scratch.bilibili.dao.VideoScratchRecordDao;
import scratch.bilibili.model.Video;
import scratch.bilibili.model.VideoScratchRecord;

@Service
public class SaveRunnable implements Runnable{

	@Autowired
	private VideoDao videoDao;
	
	@Autowired
	private VideoScratchRecordDao recordDao;
	
	@Autowired
	private PlatformTransactionManager tm;
	
	private static final int COMMIT_SIZE = 200;
	
	private BlockingQueue<Video> videos;
	
	private CyclicBarrier barrier;

	private VideoScratchRecord record;
	
	public SaveRunnable setVideos(BlockingQueue<Video> videos, VideoScratchRecord record, CyclicBarrier barrier) {
		this.videos = videos;
		this.barrier = barrier;
		this.record = record;
		return this;
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
			record.setError(record.getError() + e.toString());	//保存错误信息
			if(!await) {
				try {
					barrier.await();
				} catch (InterruptedException | BrokenBarrierException e1) {
					e1.printStackTrace();
				}
			}
		} finally {
			record.setEndTime(new Date());	//保存结束时间
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
