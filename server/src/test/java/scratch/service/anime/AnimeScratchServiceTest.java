package scratch.service.anime;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scratch.test.ContextTest;

import static org.junit.Assert.*;

public class AnimeScratchServiceTest extends ContextTest {

	@Autowired
	private AnimeScratchService animeScratchService;

	@Test
	public void run() throws Exception {
	}

	@Test
	public void isRun() throws Exception {
	}

	@Test
	public void listLogs() throws Exception {
		System.out.println(animeScratchService.listLogs());
	}

	@Test
	public void readLog() throws Exception {
	}

	@Test
	public void getRecordMap() throws Exception {
	}

	@Test
	public void listDateRecord() throws Exception {
	}

}