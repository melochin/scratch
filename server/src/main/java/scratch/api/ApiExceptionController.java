package scratch.api;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(annotations = RestController.class)
public class ApiExceptionController {

	private final static String MESSAGE = "message";

	private final static Logger log = Logger.getLogger(ApiExceptionController.class);


	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler
	public Map internalError(Exception e) {
		e.printStackTrace();
		log.error(e.getMessage());
		System.out.println(e.getMessage());
		Map map = new HashMap<>();
		map.put(MESSAGE, e.getMessage());
		return map;
	}

}
