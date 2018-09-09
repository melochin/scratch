package scratch.controller.home;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import scratch.model.ohter.UserAdapter;

@Controller
public class IndexController extends HomeController {

	/**
	 * 首页
	 *
	 * @return
	 */
	@GetMapping("/")
	public String index(Model model, @AuthenticationPrincipal UserAdapter userAdapter) {
		if (userAdapter != null) {
			return "redirect:" + USER_CENTER_URL;
		}
		addBackgroundPic(model);
		return "/index";
	}


	/**
	 * 管理员后台主页
	 *
	 * @return
	 */
	@GetMapping("/admin")
	public String admin() {
		return "/admin/index";
	}


	@GetMapping("/404")
	public String error() {
		return "base/404";
	}

	/*@GetMapping(value="/redirect")
	public String redirect(@RequestParam("url") String url, Model model) {
		// clear model
		model.asMap().clear();

		//TODO 统计redirect url 次数
		return "redirect:" + url;
	}*/


}
