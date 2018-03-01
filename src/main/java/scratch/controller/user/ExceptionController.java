package scratch.controller.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import scratch.support.web.spring.ModelUtils;

@ControllerAdvice(
		basePackages = {
			"scratch.controller.admin",
			"scratch.controller.anime",
			"scratch.controller.user"
		})
public class ExceptionController {

	private static Logger log = Logger.getLogger(ExceptionController.class);

	private final static String VIEW_ERROR = "/base/404";

	/**
	 * 处理校验异常
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler
	public String handleBeanValid(BindException e, Model model) {
		List<String> errors = new ArrayList<String>();
		for (FieldError fieldError : e.getFieldErrors()) {
			errors.add(fieldError.getDefaultMessage());
		}
		model.addAttribute("errors", errors);
		return VIEW_ERROR;
	}

	/**
	 * 处理运行时异常
	 * 异常信息输出到重定向页面
	 *
	 * @param e
	 * @param request
	 * @param ra
	 * @return
	 */
	@ExceptionHandler
	public String handleRuntimeException(RuntimeException e,
										 WebRequest request,
										 RedirectAttributes ra) {
		String referer = request.getHeader("referer");
		String error = e.getMessage();
		ModelUtils.setError(ra, error);

		// referer 如果不属于范围内的需要　
		// redirect view_error

		return "redirect:" + referer;
	}

	/**
	 * 其余异常，全部判定为404
	 * 避免用户看到系统内部错误信息，错误不输出到页面，只输出到日志文件和控制台
	 *
	 * @param e
	 * @return
	 */
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler
	public String handleException(Exception e) {
		String error = e.getMessage();
		// 打印错误信息
		log.error(error, e);
		e.printStackTrace();
		return VIEW_ERROR;
	}

}
