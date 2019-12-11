package scratch.controller.api;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import scratch.useless.ApiBrochureController;
import scratch.dao.redis.BrochureRepository;
import scratch.model.entity.Brochure;
import scratch.test.ContextTest;

public class ApiBrochureControllerTest extends ContextTest {


	@Autowired
	private ApiBrochureController controller;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private BrochureRepository brochureRepository;


	@Test
	public void list() throws Exception {
	}

	@Test
	public void add() throws Exception {
		Brochure brochure = new Brochure();
		brochure.setDescription("test");
		controller.save(brochure);
	}


}