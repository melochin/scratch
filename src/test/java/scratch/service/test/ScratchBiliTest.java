package scratch.service.test;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.service.specific.ScratchBili;
import scratch.test.ContextClass;

public class ScratchBiliTest extends ContextClass{

	@Autowired
	private ScratchBili scratch;
	
	@Test
	public void scratchBiliTest() throws MalformedURLException, IOException {
		scratch.run(73);
	}
	
}
