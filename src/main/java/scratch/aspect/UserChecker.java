package scratch.aspect;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import scratch.exception.PrivilegeException;
import scratch.model.User;
import scratch.service.UserService;
import scratch.support.CipherSupport;
import scratch.support.GlobalSession;

/**
 * 问题1：配置正常，AOP无法运行，没有生成bean
 * @author Admin
 *
 */
@Component
@Aspect()
public class UserChecker {

	@Autowired
	private CipherSupport cipher;
	
	@Autowired
	private UserService userService;
	
	@Pointcut("execution(* scratch.controller.HomeController.mainPage(..)) || "
			+ "execution(* scratch.controller.SearchInfoController.*(..)) || "
			+ "execution(* scratch.controller.SearchWordController.*(..)) || "
			+ "execution(* scratch.controller.ScratchController.*(..))")
	public void checker(){}
	
	//代理中不能使用checked异常
	@Before("checker()")
	public void checkUser(){
		HttpServletRequest request =((ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes())
					.getRequest(); 
        HttpSession session = request.getSession();
        
		//检查session中是否存在用户信息
        User user = getUserBySession(session);
        if(user == null) {
            //检查cookie是否存在用户信息
        	user = getUserByCookie(request.getCookies());	
        	session.setAttribute(GlobalSession.USER, user);
        }
        
        if(user == null) {
        	throw new PrivilegeException("请尝试登录后，再次访问页面");
        }
        if(!"1".equals(user.getStatus())) {
        	throw new PrivilegeException("账号尚未验证，无法访问页面");
        }
        
        return;
	}
	
	//为什么拦截不到？因为Service处于applicationContext上下文中，不处于代理的范围
	//解决方法：在applicationContext中引入
	//	<context:component-scan base-package="scratch.aspect" />
	//	<aop:aspectj-autoproxy/>
	@Around("@annotation(scratch.aspect.PasswordEncode)")
	public Object encodePswd(ProceedingJoinPoint pjp) throws Throwable {
		System.out.println(pjp);
		Object[] args = pjp.getArgs();
		for(Object a : args) {
			if(User.class.equals(a.getClass())) {
				User u = (User)a;
				String psdw = u.getPassword();
				if(u.getPassword() != null) {
					u.setPassword(cipher.encode(psdw));
				}
			}
		}
		return pjp.proceed(args);
	}
	
	
	private User getUserBySession(HttpSession session) {
		User user = (User) session.getAttribute(GlobalSession.USER);
		if(user == null) return null;
		String username = user.getUsername();
		if(username == null || username.trim().equals("")) return null;
		return user;
	}
	
	/**
	 * 从Cookie中获取user、userEncode信息
	 * @param cookies
	 * @return
	 */
	private User getUserByCookie(Cookie[] cookies) {
		if(cookies == null) return null;
		String userId = null;
		String userEncode = null;
        for(Cookie c : cookies){
        	if("user".equals(c.getName())) {
        		userId = c.getValue();
        	}
        	if("userEncode".equals(c.getName())) {
        		userEncode = c.getValue();
        	}
        }
        if(userId == null || userEncode == null) return null;
        
        if(!cipher.decode(userEncode).equals(userId)) return null;
        
        return userService.getById(Long.valueOf(userId));
	}
	
}
