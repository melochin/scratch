package scratch.controller.exception;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import scratch.service.ListenerService;
import scratch.support.web.JsonResult;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;

@RestControllerAdvice(annotations = RestController.class)
public class ApiExceptionController {

	private final static String MESSAGE = "error";

	private final static Logger log = Logger.getLogger(ApiExceptionController.class);

	@Autowired
	private ListenerService service;

	@ExceptionHandler
	public void IOError(IOException e, HttpServletRequest request) {
		String sessionId = request.getSession().getId();
		service.remove(sessionId);
	}

	/**
	 * 处理api异常　返回 json {error : 'error'}
	 *
	 * @param e
	 * @return
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler
	public JsonResult internalError(Exception e) {
		e.printStackTrace();
		return new JsonResult().setError(e.getMessage());
	}

}
