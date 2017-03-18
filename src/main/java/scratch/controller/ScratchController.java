package scratch.controller;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import scratch.service.specific.ScratchBili;

@Controller
public class ScratchController {
	
	@Autowired
	private ScratchBili scratchBili;
	
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
	
}
