package scratch.support;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;


public class FileUtilsTest {

	@Test
	public void test() throws IOException {
		File file = new File("haha.text");
		System.out.println(file.getCanonicalPath());
		System.out.println(file.getAbsolutePath());
		System.out.println(file.getPath());
		
		file = new File("C:wawa", "/wawa/hah");
		System.out.println(file.getAbsolutePath());
	}
	
	@Test
	public void createFileTest() {
		String url = "https://www.baidu.com/img/bd_logo1.png";
		String fileName = "test.png";
		
		File file = new File(fileName);
		if(file.exists()) {
			Assert.assertTrue(file.delete());	
		}
		
		try {
			FileUtils.downloadFile(url, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Assert.assertTrue(file.exists());
		file.delete();
		
		fileName = "bd_logo1.png";
		
		file = new File(fileName);
		if(file.exists()) {
			Assert.assertTrue(file.delete());	
		}
		
		try {
			FileUtils.downloadFile(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Assert.assertTrue(file.exists());
		file.delete();
	}

}
