package scratch.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import scratch.test.ContextTest;

public class AnimeFocusControllerTest extends ContextTest{
	
	@Test
	public void queryTest() throws Exception {
		mvc.perform(get("/anime/focus")
				.with(getUser()))
		.andExpect(view().name("/anime/focus"))
		.andExpect(model().attributeExists("animeList"));
	}
	
	@Rollback
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Test
	public void addTest() throws Exception {
		mvc.perform(post("/anime/focus/add")
				.param("animeId", "1")
				.with(getUser()))
		.andExpect(view().name("redirect:/anime/focus"));
	}
	
	@Rollback
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Test
	public void deleteTest() throws Exception {
		mvc.perform(post("/anime/focus/delete")
				.param("animeId", "1")
				.with(getUser()))
		.andExpect(view().name("redirect:/anime/focus"));
	}

}
