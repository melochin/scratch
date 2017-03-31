package scratch.service.specific;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import scratch.model.SearchInfo;
import scratch.service.bilibili.Scratch;

public class ScratchBiliRunnable implements Runnable{

	private ScratchBiliVedio info = new ScratchBiliVedio();
	
	private BlockingQueue<URL> urlQueue;
	
	private BlockingQueue<SearchInfo> infoQueue;
	
	public ScratchBiliRunnable(BlockingQueue<URL> urlQueue, 
			BlockingQueue<SearchInfo> infoQueue) {
		this.urlQueue = urlQueue;
		this.infoQueue = infoQueue;
	}
	
	@Override
	public void run() {
		try {
			long old = System.currentTimeMillis();
			System.out.println(old);
			while(urlQueue.size() > 0) {
				URL url = urlQueue.take();
				List<SearchInfo> list = info.read(url);
				infoQueue.addAll(list);
			}
			System.out.println("共抓取" + infoQueue.size() + "项数据");
			System.out.println(System.currentTimeMillis());
			long cur = System.currentTimeMillis() - old;
			System.out.println("共耗时:" + cur);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) throws MalformedURLException {
		
		BlockingQueue<SearchInfo> infos1 = new LinkedBlockingQueue<>();
		BlockingQueue<URL> list1 = new LinkedBlockingQueue();
		
		BlockingQueue<SearchInfo> infos2 = new LinkedBlockingQueue<>();
		BlockingQueue<URL> list2 = new LinkedBlockingQueue();
		
		for(int i=1; i<100; i++) {
			String vedio = "http://api.bilibili.com/archive_rank/getarchiverankbypartion?callback=callback&type=jsonp&tid=82&_=1490537389048&pn=";
			list1.add(new URL(vedio + i));
		}
		
		for(int i=1; i<100; i++) {
			String vedio = "http://api.bilibili.com/archive_rank/getarchiverankbypartion?callback=callback&type=jsonp&tid=71&_=1490537389048&pn=";
			list2.add(new URL(vedio + i));
		}
		
		ExecutorService e = Executors.newCachedThreadPool();
		e.execute(new ScratchBiliRunnable(list1, infos1));
		e.execute(new ScratchBiliRunnable(list2, infos2));
	}
}

