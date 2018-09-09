package scratch.support.web;

import org.junit.Test;

import static org.junit.Assert.*;

public class HttpConnectionTest {

	@Test
	public void connect() throws Exception {

		HttpConnection httpConnection = new HttpConnection();
		String html = httpConnection.connect("http://www.{1}.wang/", "dilidili");
		System.out.println(html);


	}

}