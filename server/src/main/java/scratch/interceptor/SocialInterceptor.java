package scratch.interceptor;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scratch.support.web.spring.SessionUtils;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import scratch.model.entity.User;

public class SocialInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		return true;
	}

	/**
	 * 拦截成功拦截的视图名，进行重定向
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		String viewName = modelAndView.getViewName();
		Map<String, Object> model = modelAndView.getModel();
		
		if(viewName == null || viewName.indexOf("Connected") == -1) {
			return;
		}
		
		//判断是否存在Connection
		if(!model.containsKey("connections")) {
			return;
		}
		List<Connection<?>> connections = (List<Connection<?>>) model.get("connections");
		if(connections.isEmpty()) {
			return;
		}
		
		//获取用户信息，存入Session中
		ConnectionData data = connections.get(0).createData();
		User user = new User(data.getDisplayName());
		user.setStatus("1");
		
		modelAndView.setViewName("redirect:/");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
	}

}
