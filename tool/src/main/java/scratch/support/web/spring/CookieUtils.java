package scratch.support.web.spring;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

public class CookieUtils {

	private static HttpServletResponse getResponse() {
		return ((ServletWebRequest)RequestContextHolder.getRequestAttributes()).getResponse();
	}
	
	private static HttpServletRequest getRequest() {
		return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
	}
	
	public static Cookie[] getCookies() {
		return getRequest().getCookies();
	}
	
	public static String getValue(String name) {
		Cookie[] cookies = getCookies();
		if(cookies == null) return null;
        for(Cookie c : cookies){
        	if(name.equals(c.getName())) {
        		return c.getValue();
        	}
        }
        return null;
	}
	
	public static void addCookie(Cookie cookie) {
		getResponse().addCookie(cookie);
	}
	
}
