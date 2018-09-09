package scratch.support;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {
	
	public static String createDir(String path, String dirName){
		
		String regex = "[/\\:|\\*?\\.]";
		dirName = dirName.replaceAll(regex, "");
		
		File dir = new File(path + dirName);

		if (dir.exists())
			return dir.getAbsolutePath();

		if (dir.mkdirs())
			return dir.getAbsolutePath();

		return null;
	}
	
	public static void downloadFile(String url, Map<String, String> config) throws IOException {
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
		downloadFile(url,config, file);
	}
	
	/**
	 * 将url的内容写到file里
	 * @param url
	 * @param file
	 * @throws IOException 
	 */
	public static void downloadFile(String url, Map<String,String> config, File file) throws IOException {
		// 建立Connection
		HttpURLConnection con = (HttpURLConnection)new URL(url).openConnection();
		con.setRequestProperty("User-agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Mobile Safari/537.36");
		if(config != null) {
			for(Entry<String,String> entry : config.entrySet()) {
				con.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}
		
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

	public static File getRandomFile(String path) {
		File file = new File(path);
		if(!file.isDirectory()) return null;
		File[] files = file.listFiles(pathname -> !pathname.isDirectory());
		if(files == null || files.length == 0) return null;
		Random random = new Random();
		return files[random.nextInt(files.length)];
	}

	public static String current() {
		return new File("").getAbsolutePath();
	}

	public static File save(String path, InputStream inputStream) throws IOException {

		File file = new File(path);
		if(!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream outputStream = new FileOutputStream(file);

		byte[] data = new byte[255];
		int result = -1;

		while(true) {
			result = inputStream.read(data, 0, 255);
			if(result == -1) {
				break;
			}
			outputStream.write(data, 0, result);
		}
		inputStream.close();
		outputStream.flush();
		outputStream.close();

		return file;
	}

}
