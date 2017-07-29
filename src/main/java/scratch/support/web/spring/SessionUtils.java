package scratch.support.web.spring;

import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SessionUtils {

	public static HttpSession getSession() {
		return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())
				.getRequest()
				.getSession();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getAttribute(String name, Class<?> T) {
		return (T) getSession().getAttribute(name);
	}
	
	public static void setAttribute(String name, Object value) {
		getSession().setAttribute(name, value);
	}
	
	public static void removeAttribute(String name) {
		getSession().removeAttribute(name);
	}
	
}
