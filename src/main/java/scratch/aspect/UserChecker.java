package scratch.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import scratch.exception.PrivilegeException;
import scratch.model.User;
import scratch.service.UserService;
import scratch.support.CipherSupport;
import scratch.support.CookieSupport;
import scratch.support.SessionSupport;

/**
 * 1.核对用户身份
 * 2.用户密码加密
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
	
	@Around("@annotation(scratch.aspect.UserRole)")
	public void checkUser(ProceedingJoinPoint pjp){
		//读取注解，通过注解判断相应的用户权限
		Annotation[] annotations = getAnnotations(pjp);
		Role role = Role.User;
		for(Annotation a : annotations) {
			if (a instanceof UserRole) {
				if(((UserRole) a).value() == Role.Admin) {
					role = Role.Admin;
					break;
				}
			}
		}
		//从Session中读取用户信息
		//读不取不到，则从Cookie中读取用户信息
        User user = SessionSupport.getUser();
        if(user == null) {
        	user = CookieSupport.getUser();
        	if(user != null) {
        		user = userService.getById(Long.valueOf(user.getUserId()));
        		//Cookie中获取信息后，保存到Session中
        		SessionSupport.setUser(user);
        	}
        }
        //核对用户是否存在
        if(user == null) {
        	throw new PrivilegeException("请先登录");
        }
        //核对用户状态
        if(!"1".equals(user.getStatus())) {
        	throw new PrivilegeException("该账号尚未激活");
        }
        //管理员才能使用的页面，核对用户账号
        if(role == Role.Admin && !user.getUsername().equals("admin")) {
        	throw new PrivilegeException("无效页面"); 
        } 
        return;
	}

	private Annotation[] getAnnotations(ProceedingJoinPoint pjp) {
		MethodSignature sign = (MethodSignature) pjp.getSignature();
		Method method = sign.getMethod();
		return method.getAnnotations();
	}
	
	/**
	 * 该通知主要用于Service层的密码加密
	 * Service的Bean必须与Aop代理处于同Bean容器中，否则Aop无法代理
	 * 解决：在相应上下文中加入Aop代理
	 * <context:component-scan base-package="scratch.aspect" />
	 * <aop:aspectj-autoproxy/>
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
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
	
}
