package resource.test;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import scratch.test.ContextTest;

public class ResourceTest extends ContextTest {

	@Test
	public void fileReader() throws IOException {
		Resource resource = new FileSystemResource("F:/Workspaces/MyEclipse 2015/scratch/src/test/java/resource/test/ResourceTest.java");
		System.out.println(ResourceToString(resource));
	}
	
	@Test
	public void UrlReader() throws IOException {
		Resource resource = new UrlResource("http://www.baidu.com");
		System.out.println(ResourceToString(resource));
	}
	
	public String ResourceToString(Resource resource) throws IOException {
		InputStream stream = resource.getInputStream();
		StringBuilder sb = new StringBuilder();
		
		byte[] bs = new byte[1024];
		int len = -1;
		while((len = stream.read(bs)) != -1) {
			sb.append(new String(bs, 0, len));
		}
		
		return sb.toString();
	}
}
