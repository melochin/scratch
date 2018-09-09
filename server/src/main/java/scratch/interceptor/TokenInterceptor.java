package scratch.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import scratch.service.TokenService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenInterceptor implements HandlerInterceptor {

	@Autowired
	private TokenService tokenService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		//
		// 预检请求
		if(request.getMethod().equals("OPTIONS")) {

			response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
			response.setHeader("Access-Control-Allow-Headers", "authorization, Content-Type, x-requested-with");
			response.setHeader("Access-Control-Max-Age", "1728000");
			return false;
		}

		// 跨域
		if(request.getHeader("Origin") != null) {
			response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			if(request.getRequestURL().indexOf("/api/admin") > 0) {
				String token = request.getHeader("Authorization");
				tokenService.verify(token);
			}
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {

	}
}
