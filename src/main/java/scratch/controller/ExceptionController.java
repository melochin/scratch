package scratch.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import scratch.exception.PrivilegeException;

@ControllerAdvice
public class ExceptionController {
	
	/**
	 * �����û�Ȩ���쳣
	 * @param e
	 * @param model
	 * @return
	 */
	@ExceptionHandler
	public ModelAndView hanlderPrivilege(PrivilegeException e, Model model) {
		model.addAttribute("error", e.getMessage());
		//��/�����Զ�λ������
		model.addAttribute("jump", "user/login");
		return new ModelAndView("common_message");
	}
	
	/**
	 * ���������쳣
	 * @param e
	 * @param model
	 * @return
	 */
	@ExceptionHandler
	public ModelAndView hanlderException(Exception e, Model model) {
		String error = "";
		error = e.getMessage();
		e.printStackTrace();
		model.addAttribute("error", error.isEmpty() ? "ҳ�淢������" : "ҳ�淢������:" + error);
		return new ModelAndView("common_message");
	}
	
}
