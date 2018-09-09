package scratch.controller.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import scratch.service.WebFileService;

public class HomeController {

	private static final String WEB_INF_RESOURCE_PIC_HOME = "/WEB-INF/resource/pic/home";

	protected final static String USER_CENTER_URL = "/center";

	protected final static String INDEX_URL = "/";

	@Autowired
	private WebFileService fileService;

	/**
	 * Model: backgroundPic - String
	 * @param model
	 */
	public void addBackgroundPic(Model model) {
		if(!model.containsAttribute("backgroundPic")) {
			String backgroundPic = fileService.getRandomFile(WEB_INF_RESOURCE_PIC_HOME).getName();
			if(backgroundPic != null) {
				model.addAttribute("backgroundPic", backgroundPic);
			}
		}
	}

}
