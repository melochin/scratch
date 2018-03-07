package scratch.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import scratch.support.regex.RegexMatch;

public class RegexMatchTest {

	@Test
	public void RegexMatchTest() throws MalformedURLException {
		Map<String, String> request = new HashMap<>();
		request.put("mid", "10183080");
		request.put("_", String.valueOf(System.currentTimeMillis()));
		RegexMatch r = new RegexMatch(new URL("https://space.bilibili.com/ajax/member/GetInfo"),
				"post",request);
		System.out.println(r.getSourceText());
	}
}
