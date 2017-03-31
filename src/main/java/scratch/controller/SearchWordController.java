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

import scratch.model.SearchKeyword;
import scratch.model.SearchTag;
import scratch.model.User;
import scratch.model.dictionary.SearchType;
import scratch.service.SearchKeywordService;
import scratch.service.SearchTagService;
import scratch.service.SearchTypeService;
import scratch.support.GlobalSession;

@Controller
@RequestMapping("/search")
public class SearchWordController {

	@Autowired
	private SearchTagService tagService;
	
	@Autowired
	private SearchTypeService typeServce;
	
	@Autowired
	private SearchKeywordService wordService;
	
	/**
	 * 标签维护表单界面
	 * @return
	 */
	@RequestMapping("/tag")
	public ModelAndView tagForm(Model model, HttpSession session){
		User user = (User) session.getAttribute(GlobalSession.USER);
		List<SearchTag> list = tagService.listByUserId(user.getUserId());
		List<SearchType> types = typeServce.list();
		model.addAttribute("types", types);
		return new ModelAndView("tag_form", "searchTags", list);
	}
	
	//标签：更新
	@RequestMapping(value="/tag", method=RequestMethod.POST)
	public @ResponseBody String updateTag(@ModelAttribute SearchTag tag, HttpSession session) {
		User user = (User) session.getAttribute(GlobalSession.USER);
		tag.setUser(user);		
		tagService.update(tag);
		return "redirect:/search/tag";
	}
	
	//标签：删除
	@RequestMapping(value="/tag/delete", method=RequestMethod.POST)
	public @ResponseBody String deleteTag(@ModelAttribute SearchTag tag) {
		tagService.delete(tag);
		return null;
	}
	
	//关键字：更新
	@RequestMapping(value="/tag/word", method=RequestMethod.POST)
	public @ResponseBody String updateWord(@ModelAttribute SearchKeyword word) {
		wordService.update(word);
		return null;
	}
	
	//关键字：删除
	@RequestMapping(value="/tag/word/delete", method=RequestMethod.POST)
	public @ResponseBody String deleteWord(@ModelAttribute SearchKeyword word) {
		wordService.delete(word);
		return null;
	}
	
}
