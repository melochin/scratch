package scratch.service.bilibili;

import org.apache.log4j.Logger;

public abstract class ScratchRunnable implements Runnable{

	protected static Logger log = Logger.getLogger(ScratchRunnable.class);
	
	private static int count = 0;
	
	private int id = count++;
	
	public abstract void scratch();
	
	@Override
	public void run() {
		scratch();
	}

	@Override
	public String toString() {
		return "Scratch " + id + " ";
	}
}
