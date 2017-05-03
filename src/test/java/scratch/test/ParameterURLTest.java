package scratch.test;

import org.junit.Assert;
import org.junit.Test;

import scratch.support.regex.ParameterURL;

public class ParameterURLTest {

	@Test
	public void parameterURLTest() {
		String url = "http://search.bilibili.com/all";
		ParameterURL pUrl = new ParameterURL(url);
		pUrl.setParameter("keyword", "A");
		pUrl.setParameter("keyword", "B");
		pUrl.setParameter("tag", "C");
		Assert.assertEquals(pUrl.getURL().toString(), "http://search.bilibili.com/all?keyword=B&tag=C");
	}
}
