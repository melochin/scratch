package scratch.support.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.test.ContextTest;

public class ThymeleafTempalteTest extends ContextTest{

	@Autowired
	private TemplateEngine template;
	
	@Test
	public void test() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("url", "www.baidu.com");
		String html = template.getContent("/mail/resetpwd.html", map);
		System.out.println(html);
	}

}
