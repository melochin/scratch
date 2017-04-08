package scratch.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import scratch.aspect.UserRole;
import scratch.bilibili.model.VideoType;
import scratch.bilibili.service.VideoTypeService;
import scratch.model.SearchKeyword;
import scratch.model.SearchTag;
import scratch.model.User;
import scratch.service.SearchKeywordService;
import scratch.service.SearchTagService;
import scratch.support.GlobalSession;
import scratch.support.SessionSupport;

@Controller
@RequestMapping("/search")
public class SearchWordController {

	@Autowired
	private SearchTagService tagService;
	
	@Autowired
	private VideoTypeService typeServce;
	
	@Autowired
	private SearchKeywordService wordService;
	
	@UserRole
	@RequestMapping("/tag")
	public ModelAndView tagForm(Model model, HttpSession session){
		User user = SessionSupport.getUser();
		List<SearchTag> list = tagService.listByUserId(user.getUserId());
		List<VideoType> types = typeServce.list(1);
		model.addAttribute("types", types);
		return new ModelAndView("tag_form", "searchTags", list);
	}
	
	@UserRole
	@RequestMapping(value="/tag", method=RequestMethod.POST)
	public @ResponseBody String updateTag(@ModelAttribute SearchTag tag, HttpSession session) {
		User user = SessionSupport.getUser();
		tag.setUser(user);		
		tagService.update(tag);
		return "redirect:/search/tag";
	}
	
	@UserRole
	@RequestMapping(value="/tag/delete", method=RequestMethod.POST)
	public @ResponseBody String deleteTag(@ModelAttribute SearchTag tag) {
		tagService.delete(tag);
		return null;
	}
	
	@UserRole
	@RequestMapping(value="/tag/word", method=RequestMethod.POST)
	public @ResponseBody String updateWord(@ModelAttribute SearchKeyword word) {
		wordService.update(word);
		return null;
	}
	
	@UserRole
	@RequestMapping(value="/tag/word/delete", method=RequestMethod.POST)
	public @ResponseBody String deleteWord(@ModelAttribute SearchKeyword word) {
		wordService.delete(word);
		return null;
	}
	
}
