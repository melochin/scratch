package scratch.service.anime;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import scratch.test.ContextTest;

public class AnimeScratchServiceTest extends ContextTest {

	@Autowired
	AnimeScratchService service;

	@Test
	public void scratchTest() throws InterruptedException {
		service.run();
		TimeUnit.SECONDS.sleep(30);
		
	}
	
//	@Test
	public void JsonTest() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writeValueAsString(new String("rara")));
		System.out.println(mapper.writeValueAsString(Arrays.asList(1,2,3,4,5)));
	}

}
