package scratch.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MultipartFile;
import scratch.support.FileUtils;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class WebFileService implements ServletContextAware {

	private ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public String getRealPath(String path) {
		return servletContext.getRealPath(path);
	}

	public File getFile(String path) {
		return new File(getRealPath(path));
	}

	public String generateUUIDFilename(MultipartFile multipartFile) {
		//获取表单上传文件的后缀名
		String originFilename = multipartFile.getOriginalFilename();
		String suffix = FileUtils.getSuffix(originFilename);
		//使用UUID作为文件名
		String filename = UUID.randomUUID().toString() + "." + suffix;
		return filename;
	}


	public File save(String path, String newFilename, MultipartFile multipartFile) throws IOException {
		String realPath = getRealPath(path);

		if(!new File(realPath).exists()) {
			new File(realPath).mkdirs();
		}

		File file = new File(FilenameUtils.concat(realPath, newFilename));
		multipartFile.transferTo(file);
		return file;
	}

	public File save(String path, InputStream inputStream) throws IOException {
		return FileUtils.save(getRealPath(path), inputStream);
	}

	public File save(String path, MultipartFile multipartFile) throws IOException {
		String newFilename = generateUUIDFilename(multipartFile);
		return save(path, newFilename, multipartFile);
	}

	public void delete(String path, String filename) {
		if(filename == null || filename.isEmpty()) return;

		File file = new File(getRealPath(path), filename);
		file.delete();
	}

	public File getRandomFile(String path) {
		String realPath = getRealPath(path);
		return FileUtils.getRandomFile(realPath);
	}


}
