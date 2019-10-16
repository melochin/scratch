package scratch.controller.home;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import scratch.model.ohter.UserAdapter;

@Controller
public class IndexController {

	/**
	 * 首页
	 * @return
	 */
	@GetMapping("/")
	public String index(@AuthenticationPrincipal UserAdapter userAdapter) {

		if (userAdapter != null) {
			return "redirect:/center" ;
		}

		return "/index";
	}


	/**
	 * 后台管理主页
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

}
