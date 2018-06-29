package scratch.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import scratch.model.SessionContext;
import scratch.model.ohter.UserAdapter;
import scratch.service.UserService;
import scratch.support.StringUtils;
import scratch.support.web.spring.SessionUtils;

public class AutoLoginInterceptor implements  HandlerInterceptor {

	@Autowired
	private UserService userService;
	
	private boolean auto = false;

	private String username;

	private String password;

	public AutoLoginInterceptor() { }
	

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if(!auto) return true;
		// ???运行了三次session里才有值？
		if(SessionUtils.containAttribute(SessionContext.USER)) return true;

		if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) return true;

		if(!"127.0.0.1".equals(request.getRemoteAddr()) && !"0:0:0:0:0:0:0:1".equals(request.getRemoteAddr())) return true;


		// 账号校验
		Authentication authentication = userService.authen(username, password);
		// 存入session
		SecurityContextHolder.getContext().setAuthentication(authentication);
		SessionUtils.setAttribute(SessionContext.USER, ((UserAdapter)authentication.getPrincipal()));
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

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAuto(boolean auto) {
		this.auto = auto;
	}
}
