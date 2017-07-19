package scratch.support;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {
	
	public static void downloadFile(String url) throws IOException {
		String[] words = url.split("/");
		String fileName = null;
		if(words.length > 1) {
			String temp = words[words.length - 1];
			if(temp.indexOf('.') >= 0) {
				fileName = temp;
			}
		}
		
		if(fileName == null) {
			fileName = String.valueOf(UUID.randomUUID());
		}
		
		File file = new File(fileName);
		downloadFile(url, file);
	}
	
	/**
	 * 将url的内容写到file里
	 * @param url
	 * @param file
	 * @throws IOException 
	 */
	public static void downloadFile(String url, File file) throws IOException {
		// 建立Connection
		HttpURLConnection con = (HttpURLConnection)new URL(url).openConnection();
		// 获取输入流
		BufferedInputStream inputStream = new BufferedInputStream(con.getInputStream());
		// 获取输出流
		FileOutputStream writer = new FileOutputStream(file);
		byte[] bytes = new byte[255];
		int len = 0;
		// 读取
		while((len = inputStream.read(bytes, 0, 255))> 0) {
			// 写出
			writer.write(bytes, 0, len);	
		}
		writer.flush();
		writer.close();
	}
	
	
	public static void uploadWithRandomName(MultipartFile multipartFile, HttpServletRequest request, String directory) {
		// get file suffix
		String originFilename = multipartFile.getOriginalFilename();
		String suffix = FileUtils.getSuffix(originFilename);
		// get file name
		String filename = UUID.randomUUID().toString() + "." + suffix;
		String path = request.getServletContext().getRealPath(directory);
		String fullFileName = FilenameUtils.concat(path, filename);
		// 保留文件后缀
		File file = new File(fullFileName);
		try {
			multipartFile.transferTo(file);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}		
	}
	

	
	public static String getSuffix(String fileName) {
		String splits[] = fileName.split("\\.");
		if(splits == null || splits.length < 2) {
			return "";
		}
		return splits[1];
	}
	
}
