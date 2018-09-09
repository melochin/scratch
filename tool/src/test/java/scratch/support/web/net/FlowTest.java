package scratch.support.web.net;

import org.junit.Test;

import java.net.MalformedURLException;

import static org.junit.Assert.*;

public class FlowTest {

	@Test
	public void test() throws MalformedURLException {

		Flow flow = new Flow();

		flow.get("http://www.dilidili.wang/").html();
		flow.get("http://www.dilidili.wang/").html();
		flow.get("https://www.bilibili.com/video/av26954059").html();
		flow.get("https://pan.baidu.com/share/init?surl=ifFN4Xqxyra3zbh6rCEQpQ").html();

		System.out.println(flow);
	}

}