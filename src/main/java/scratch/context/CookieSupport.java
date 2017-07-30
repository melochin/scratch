package scratch.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;

import scratch.model.User;
import scratch.support.cipher.CipherSupport;
import scratch.support.web.spring.CookieUtils;

public class CookieSupport {

	@Autowired
	private static CipherSupport cipher;
	
	private static int MAXAGE = 300;
	
	private static String PATH = "/";
	
	
	/**
	 * ���map��Cookie��
	 * @param map
	 */
/*	private static void addCookies(Map<String, String> map) {
		HttpServletResponse response = getResponse();
		for(Entry<String, String> e : map.entrySet()) {
			Cookie cookie = new Cookie(e.getKey(), e.getValue());
			cookie.setMaxAge(MAXAGE);
			cookie.setPath(PATH);
			response.addCookie(cookie);
		}
	}*/
	
	/**
	 * ���û���Ϣ�����Cookie�У����userId�ͼ��ܺ��userId
	 * ���⣺cookie ָ����path·������ȷ��ָ��������localhost
	 * @param user
	 */
	public static void addUser(User user) {
		Map<String, String> map = new HashMap<String, String>();
		String userId = user.getUserId().toString();
		map.put("userId", userId);
		map.put("user", cipher.encode(userId.toString()));
		
		for(Entry<String, String> entry : map.entrySet()) {
			Cookie cookie = new Cookie(entry.getKey(), entry.getValue());
			cookie.setMaxAge(MAXAGE);
			cookie.setPath(PATH);
			CookieUtils.addCookie(cookie);
		}
	}
	
	public static User getUser() {
		Cookie[] cookies = CookieUtils.getCookies();
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
        
        User user = new User();
        user.setUserId(Long.valueOf(userId));
        return user; 
	}
	
}
