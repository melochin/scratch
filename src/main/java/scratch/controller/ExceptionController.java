package scratch.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import scratch.exception.PrivilegeException;
import static scratch.exception.PrivilegeException.*;

@ControllerAdvice
public class ExceptionController {
	
	private static Logger log = Logger.getLogger(ExceptionController.class);
	
	private final static String VIEW_ERROR = "/base/message";
	
	/**
	 * 处理权限
	 * @param e
	 * @param model
	 * @return
	 */
	@ExceptionHandler
	public String hanlderPrivilege(PrivilegeException e) {
		String error = e.getMessage();
		if(error.equals(NOLOGIN)) {
			return "redirect:/user/login";
		} else if (error.equals(NOACTIVI)) {
			return "redirect:/user/login";
		}
		
		return "redirect:/";
	}
	
	/**
	 * 处理校验异常
	 * @param e
	 * @return
	 */
	@ExceptionHandler
	public String handleBeanValid(BindException e, Model model) {
		List<String> errors = new ArrayList<String>();
		for(FieldError fieldError : e.getFieldErrors()) {
			errors.add(fieldError.getDefaultMessage());
		}
		model.addAttribute("errors", errors);
		return VIEW_ERROR;
	}
	
	/**
	 * 处理所有异常
	 * @param e
	 * @param model
	 * @return
	 */
	@ExceptionHandler
	public String hanlderException(Exception e, Model model) {
		String error = e.getMessage();
		log.error(error, e);
		model.addAttribute("error", error.isEmpty() ? "程序发生异常" : "异常错误:" + error);
		return VIEW_ERROR;
	}
	
}
