package scratch.aspect;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import scratch.exception.PrivilegeException;

@ControllerAdvice
public class ExceptionController {
	
	/**
	 * 处理用户权限异常
	 * @param e
	 * @param model
	 * @return
	 */
	@ExceptionHandler
	public ModelAndView hanlderPrivilege(PrivilegeException e, Model model) {
		model.addAttribute("error", e.getMessage());
		//少/，绝对定位？？？
		model.addAttribute("jump", "user/login");
		return new ModelAndView("common_message");
	}
	
	/**
	 * 处理所有异常
	 * @param e
	 * @param model
	 * @return
	 */
	@ExceptionHandler
	public ModelAndView hanlderException(Exception e, Model model) {
		String error = "";
		error = e.getMessage();
		e.printStackTrace();
		model.addAttribute("error", error.isEmpty() ? "页面发生错误" : "页面发生错误:" + error);
		return new ModelAndView("common_message");
	}
	
}
