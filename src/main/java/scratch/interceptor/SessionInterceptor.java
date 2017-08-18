package scratch.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import scratch.dao.UserDao;
import scratch.model.entity.User;
import scratch.context.SessionContext;

public class SessionInterceptor implements  HandlerInterceptor {

	private User user;
	
	@Autowired
	private UserDao dao;
	
	private boolean autoLogin;
	
	public SessionInterceptor() {
		user = new User("consequent");
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(!autoLogin) {
			return true;
		}
		if(request.getSession().getAttribute(SessionContext.USER) == null ) {
			if("localhost".equals(request.getServerName()) || 
					"127.0.0.1".equals(request.getRemoteHost())) {
				user = dao.getByName(user);
				request.getSession().setAttribute(SessionContext.USER, user);
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void setAutoLogin(boolean autoLogin) {
		this.autoLogin = autoLogin;
	}

}
