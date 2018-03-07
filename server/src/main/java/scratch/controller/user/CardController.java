package scratch.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CardController {

	@GetMapping("/card")
	public String index() {
		return "/card/index";
	}
}
