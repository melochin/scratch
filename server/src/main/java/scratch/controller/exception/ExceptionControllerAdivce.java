package scratch.controller.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
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
				"scratch.controller.home",
				"scratch.controller.admin",
				"scratch.controller.user",
		})
public class ExceptionControllerAdivce {

	private static Logger log = Logger.getLogger(ExceptionControllerAdivce.class);

	private final static String VIEW_NOT_FOUND = "/base/404";

	private final static String VIEW_ERROR = "/base/500";

	/**
	 * 处理校验异常
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler
	public String handleBeanValid(BindException e, Model model) {

		e.printStackTrace();

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

		log.error("", e);
		String referer = request.getHeader("referer");
		String error = e.getMessage();
		ModelUtils.setError(ra, error);

		// referer 如果不属于范围内的需要　
		// redirect view_error

		return "redirect:" + referer;
	}

	/**
	 * 其他异常，判定为500
	 * 1. 错误信息输出到500页面
	 * 2. 错误信息输出到日志文件
	 * @param e
	 * @return
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler
	public String handleException(Exception e, Model model) {

		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String errorMessage = sw.toString();

		// 输出到日志
		log.error("", e);
		// 输出到页面
		model.addAttribute("error", errorMessage);
		return VIEW_ERROR;
	}

}
