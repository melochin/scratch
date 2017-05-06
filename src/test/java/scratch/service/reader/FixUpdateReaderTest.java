package scratch.service.reader;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.test.ContextTest;

public class FixUpdateReaderTest extends ContextTest {

	private FixUpdateReader reader = new FixUpdateReader();
	
	@Test
	public void test() {
		reader.read();
	}

}
