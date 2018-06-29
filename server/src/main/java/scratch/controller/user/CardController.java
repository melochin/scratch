package scratch.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import scratch.dao.redis.ICardRepository;

@Controller
public class CardController {

	@Autowired
	private ICardRepository cardRepository;

	@GetMapping("/card")
	public String index() {
		return "/card/index";
	}

}
