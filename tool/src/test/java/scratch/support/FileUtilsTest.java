package scratch.support;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class FileUtilsTest {
	@Test
	public void getRandomFile() throws Exception {
		System.out.println(FileUtils.getRandomFile("../"));
	}

	@Test
	public void current() throws Exception {
		System.out.println(FileUtils.current());
	}

}