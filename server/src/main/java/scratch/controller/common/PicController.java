package scratch.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import scratch.service.WebFileService;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class PicController {

	@Autowired
	private WebFileService fileService;

	@Value("${pic.url}")
	private String url;

	private static final String WEB_INF_RESOURCE_PIC_HOME = "/WEB-INF/resource/pic/home";

	/**
	 * Model: backgroundPic - String
	 */
	@GetMapping("/background")
	public void colorfulBackground(HttpServletResponse response) throws IOException {
		File file = fileService.getRandomFile(WEB_INF_RESOURCE_PIC_HOME);

		response.setContentType("image/png");
		writeBytes(new FileInputStream(file), response.getOutputStream());
	}

	@GetMapping(value = "/pic/{filename:.+}")
	public void getPic(HttpServletResponse response,
					   @PathVariable("filename") String filename) throws IOException {

		String fullPath = url + "/" + filename;
		File file = new File(fullPath);

		// 找不到文件 返回错误
		if (file.exists() == false) {
			file = new File(url + "/default.png");
		}

		// 输出二进制流
		response.setContentType("image/png");
		writeBytes(new FileInputStream(file), response.getOutputStream());
	}

	private void writeBytes(InputStream src, OutputStream dest) throws IOException {
		while(src.available() > 0) {
			byte[] bytes = new byte[255];
			int read = src.read(bytes);
			dest.write(bytes, 0, read);
		}
		src.close();
		dest.close();
	}

}
