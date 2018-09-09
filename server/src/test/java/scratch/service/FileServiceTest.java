package scratch.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scratch.test.ContextTest;

import javax.print.DocFlavor;

import static org.junit.Assert.*;

import java.io.*;

public class FileServiceTest extends ContextTest {

	@Autowired
	private WebFileService fileService;

	@Test
	public void getRealPath() throws Exception {
		String realPath = fileService.getRealPath("/WEB-INF");
		assertTrue(realPath.contains("webapp"));
	}

	@Test
	public void getFile() {
		File file = fileService.getFile("/WEB-INF");
		assertTrue(file.exists());
		assertTrue(file.isDirectory());
	}

	@Test
	public void getReandomFile() {
		File file = fileService.getRandomFile("/WEB-INF");
		assertNotNull(file);
	}

	@Test
	public void save() throws IOException {
		StringBufferInputStream inputStream = new StringBufferInputStream("test");
		File file = fileService.save("/WEB-INF/test.file", inputStream);
		assertNotNull(file);
	}


}