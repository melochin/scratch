package scratch.service.anime;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scratch.test.ContextTest;

import static org.junit.Assert.*;

public class AnimeScratchTaskTest extends ContextTest {

	@Autowired
	private AnimeScratchTask scratchTask;

	@Test
	public void run() throws Exception {
		scratchTask.run();
	}

	@Test
	public void isRun() throws Exception {
	}

}