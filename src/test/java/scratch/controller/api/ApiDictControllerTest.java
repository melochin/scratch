package scratch.controller.api;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import scratch.model.entity.Dict;
import scratch.test.ContextTest;

@Transactional
public class ApiDictControllerTest extends ContextTest {

	private Dict dict;
	private ObjectMapper mapper = new ObjectMapper();

	{
		dict = new Dict();
		dict.setCode("1");
		dict.setParentCode("09");
		dict.setValue("Test");
	}

	//@Test
/*	public void showIndex() throws Exception {
		MvcResult result = mvc.perform(get("/dic"))
				.andExpect(status().isOk())
				.andReturn();
		Assert.assertTrue(result.getModelAndView().getViewName() == "dic_index");
		Assert.assertTrue(result.getModelAndView().getModel().containsKey("dictionaries"));
	}*/

	@Rollback
	@Test
	public void delete() throws Exception {
		save();
		mvc.perform(
				MockMvcRequestBuilders.delete("/api/admin/dics/{parentCpde}/{code}}",
						dict.getParentCode(), dict.getCode())
						.with(getAdmin())
						.with(csrf().asHeader()))
				.andExpect(status().isOk());
	}

	@Rollback
	@Test
	public void list() throws Exception {
		mvc.perform(get("/api/admin/dics")
				.param("parentCode", "01")
				.with(getAdmin()))
				.andExpect(status().isOk());
		mvc.perform(get("/api/admin/dics")
				.with(getAdmin()))
				.andExpect(status().isOk());
	}

	@Rollback
	@Test
	public void modify() throws Exception {
		save();
		dict.setValue("测试");
		String json = mapper.writeValueAsString(dict);
		mvc.perform(put("/api/admin/dics")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
				.with(getAdmin())
				.with(csrf().asHeader()))
				.andExpect(status().isOk());
	}

	@Rollback
	@Test
	public void save() throws Exception {
		String json = mapper.writeValueAsString(dict);
		mvc.perform(post("/api/admin/dics")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json)
				.with(getAdmin())
				.with(csrf().asHeader()))
				.andExpect(status().isOk());
	}

}
