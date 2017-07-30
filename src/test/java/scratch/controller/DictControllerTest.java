package scratch.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import scratch.test.ContextTest;

public class DictControllerTest extends ContextTest {
	
	@Test
	public void showIndex() throws Exception {
		MvcResult result = mvc.perform(get("/dic"))
			.andExpect(status().isOk())
			.andReturn();
		Assert.assertTrue(result.getModelAndView().getViewName() == "dic_index");
		Assert.assertTrue(result.getModelAndView().getModel().containsKey("dictionaries"));
	}
	
	
}
