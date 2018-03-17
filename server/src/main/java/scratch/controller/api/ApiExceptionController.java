package scratch.controller.api;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import scratch.service.ListenerService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(annotations = RestController.class)
public class ApiExceptionController {

	private final static String MESSAGE = "error";

	private final static Logger log = Logger.getLogger(ApiExceptionController.class);


	@Autowired
	private ListenerService service;

	@ExceptionHandler
	public void IOError(IOException e, HttpServletRequest request) {
		System.out.println("处理任务");
		String sessionId = request.getSession().getId();
		System.out.println("sessionId:" + sessionId);
		service.remove(sessionId);

	}

	/**
	 * 处理api异常　返回 json {error : 'error'}
	 *
	 * @param e
	 * @return
	 */
/*	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler
	public Map internalError(Exception e) {
		e.printStackTrace();
		log.error(e.getMessage());
		System.out.println(e.getMessage());
		Map map = new HashMap<>();
		map.put(MESSAGE, e.getMessage());
		return map;
	}*/

}
