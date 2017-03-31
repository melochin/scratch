package scratch.support;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import scratch.model.User;

@Component
public class SessionSupport {

	public static HttpSession getSession() {
		return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes())
				.getRequest()
				.getSession();
	}
	
	public static User getUser() {
		return (User)getSession().getAttribute(GlobalSession.USER);
	}
	
	public static void setUser(User user) {
		user.setPassword("");
		getSession().setAttribute(GlobalSession.USER, user);
	}
	
	public static void removeUser() {
		getSession().removeAttribute(GlobalSession.USER);
	}
	
}
