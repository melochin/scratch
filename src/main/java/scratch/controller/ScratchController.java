package scratch.controller;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import scratch.model.User;
import scratch.service.ScratchService;
import scratch.service.specific.ScratchBili;
import scratch.support.GlobalSession;

@Controller
public class ScratchController {
	
	@Autowired
	private ScratchBili scratchBili;
	
	@Autowired
	private ScratchService scratchSerivce;
	
	@RequestMapping(value="/scratch/{tagId}", method=RequestMethod.GET)
	public String scratchBiliByTag(@PathVariable long tagId){
		try {
			scratchBili.run(tagId);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/";
	}
	
	//提示是否进行抓取数据
	@RequestMapping(value="/scratch/alert", method=RequestMethod.GET)
	public @ResponseBody boolean alert(HttpSession session) {
		User user = (User)session.getAttribute(GlobalSession.USER);
		return scratchSerivce.alertSearch(user.getUserId());
	}
}
