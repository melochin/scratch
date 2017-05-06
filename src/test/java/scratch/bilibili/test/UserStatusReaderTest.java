package scratch.bilibili.test;

import org.junit.Test;

import scratch.service.reader.AnimeUpdateReader;
import scratch.service.reader.UserStatusReader;
import scratch.test.ContextTest;

public class UserStatusReaderTest extends ContextTest{

	private AnimeUpdateReader reader = new AnimeUpdateReader();
	
	@Test
	public void statusTest() {
		System.out.println(reader.read());
	}
	
}
