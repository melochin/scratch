package scratch.service;

import java.io.File;

import org.springframework.stereotype.Service;

@Service
public class FileService {

	public String createDir(String path, String dirName){
		
		String regex = "[/\\:|\\*?\\.]";
		dirName = dirName.replaceAll(regex, "");
		
		File dir = new File(path + dirName);

		if (dir.exists())
			return dir.getAbsolutePath();

		if (dir.mkdirs())
			return dir.getAbsolutePath();

		return null;
	}
}
