package scratch.api.etest;

import com.fasterxml.jackson.databind.ObjectMapper;
import scratch.support.web.HttpConnection;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EtestImpl implements Etest {

	private final static String CHECK_IMG_URL = "https://testdaf.etest.net.cn/checkImage.do";

	private final static String BOOK_URL = "https://testdaf.etest.net.cn/bookAddr.do";

	private final static String LOGIN_URL = "https://testdaf.etest.net.cn/login.do";

	private final static String LOGIN_FORM_URL = "https://testdaf.etest.net.cn/login.do";

	private final static String BOOK_FORM_URL = "https://testdaf.etest.net.cn/chooseAddr.do";

	private final static Logger log = Logger.getLogger(EtestImpl.class);

	private final int MAX_TRY = 1000;

	private Map<String, String> cookies = new HashMap<String, String>();

	private StudentInfo studentInfo;

	private HttpConnection httpConnection = new HttpConnection();


	public EtestImpl(StudentInfo studentInfo) {
		this.studentInfo = studentInfo;
		PropertyConfigurator.configure("src/main/java/log4j.properties");
	}

	/**
	 * 读取校验码
	 * @return
	 */
	@Override
	public String getCheckImgCode() {

		// 提前准备好的校验码与图片的映射关系
		ObjectMapper map = new ObjectMapper();
		Map<String, String> imgMaps = new HashMap<String, String>();
		try {
			imgMaps = map.readValue(new File("etest.json"), Map.class);
		} catch (IOException e) {
			log.error("读取etest.json失败");
			return null;
		}

		// 通过多次请求校验码图片，来匹配已经存在的校验码
		String imgCode = null;
		for(int i=0; i<MAX_TRY; i++) {
			try {
				String imgUrl = doGetCheckImgUrl();
				imgCode = findImageCode(imgUrl, imgMaps);
				if(imgCode != null) {
					System.out.println("请求验证码次数:" + i);
					break;
				}
			} catch (IOException e) {
				log.error("读取验证码失败:" + e.getMessage());
			}
		}
		return imgCode;
	}

	private String findImageCode(String imgUrl, Map<String, String> imgMaps) {
		if(imgMaps.containsKey(imgUrl)) {
			return imgMaps.get(imgUrl);
		}
		return null;
	}

	private String doGetCheckImgUrl() throws IOException {
		String json = httpConnection.connect(CHECK_IMG_URL);
		if(StringUtils.isEmpty(json)) {
			log.error("读取验证码失败");
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = mapper.readValue(json, Map.class);
		return map.get("chkImgFilename");
	}


	@Override
	public boolean book() {
		String html = getBookForm();
		String csrfToken = getCSRFToken(html);
		String imgCode = getCheckImgCode();
		return postBook(studentInfo.getKdid(), studentInfo.getNeeaId(), imgCode, csrfToken);
	}
	/**
	 * 自动加入最新的cookie
	 * @return
	 */
	private HttpConnection getConnection() {
		return this.httpConnection;
	}

	private String getBookForm() {
		HttpConnection connection = getConnection();
		String url = BOOK_FORM_URL + "?neeaId=" + studentInfo.getNeeaId();
		System.out.println("get url:" + url);
		String html = connection.connect(url);
		if(html.indexOf("无效的登录") > 0) {
			throw new RuntimeException("会话失效：无效登录");
		}
		if(html.indexOf("预定座位") == -1) {
			throw new RuntimeException("出现异常：" + html);
		}
		return html;
	}

	private boolean postBook(String kdid, String neeaId,
							 String checkImgCode, String csrfToken) {
		HttpConnection connection = new HttpConnection();
		String cookie = connection.getCookie().toString();
		System.out.println("post url:" + BOOK_URL + ";\n" +
				"param:" + "kdid=" + kdid + ";" +
				"neeaId=" + neeaId + ";" +
				"checkImgeCode=" + checkImgCode + ";" +
				"CSRFToken=" + csrfToken + ";\n" +
				"header:" + "Cookie=" + cookie + ";");
		connection.method("post")
				.param("kdid", kdid)
				.param("neeaId", neeaId)
				.param("checkImgCode", checkImgCode)
				.param("CSRFToken", csrfToken)
				.header("Cookie", cookie);
		String html = "";
		html = connection.connect(BOOK_URL);

		if(html.indexOf("没有剩余座位") >= 0) {
			System.out.println("kdid:" + kdid +",座位已满");
			return false;
		} else {
			System.out.println(html);
			throw new RuntimeException("出现异常：" + html);
		}
	}

	private boolean canBook(String html) {
/*		Document document = Jsoup.parse(html);
		Elements elements = document.select(".btn[id='" +
				studentInfo.getKdid() + "']");
		if(elements.size() != 1) {
			throw new RuntimeException("解析座位状态失败");
		}
		String value = elements.get(0).val();
		System.out.println(value);
		if("座位暂满".equals(value)) {
			return false;
		}
		return true;*/
		return true;
	}


	@Override
	public int login() {

		if(studentInfo.isLogin()) {
			return 1;
		}

		// 跨域Token
		String loginHtml = getLoginForm();
		String csrfToken = getCSRFToken(loginHtml);
		// 先请求验证码
		String checkImgCode = getCheckImgCode();
		// 获取登录信息
		String userId = studentInfo.getUsername();
		String password = studentInfo.getPassword();
		// 获取登录token
		String token =  userId.toUpperCase() + password;
		token = DigestUtils.md5DigestAsHex(token.getBytes());
		int result = postLogin(userId, token, checkImgCode, csrfToken);
		if(result == 1) {
			studentInfo.setLogin(true);
		}
		return result;
	}

	private int postLogin(String userId, String token, String checkImgCode, String csrfToken) {
		// post
		HttpConnection connection = getConnection();
		// get cookie
		String cookie = connection.getCookie().toString();
		System.out.println("post url:" + LOGIN_URL + ";\n" +
				"param:" + "checkImgeCode=" + checkImgCode + ";" +
				"userId=" + userId + ";" +
				"token=" + token + ";" +
		 		"CSRFToken=" + csrfToken + ";\n" +
		 		"header:" + "Cookie=" + cookie + ";\n");
		connection.method("post")
				.param("checkImgCode", checkImgCode)
				.param("userId", userId)
				.param("token", token)
				.param("CSRFToken", csrfToken)
				.header("Cookie", cookie);
		String html = connection.connect(LOGIN_URL);
		if(StringUtils.isEmpty(html)) {
			System.out.println("登录成功");
			return 1;
		}
		if(html.indexOf("错误次数过多") >= 0) {
			System.out.println("账号锁定：10秒后重试登录");
			this.cookies.clear();
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return 0;
		}
		System.out.println(html);
		System.out.println("登录失败");
		return -1;
	}


	private String getLoginForm() {
		HttpConnection connection = getConnection();
		String loginHtml = connection.connect(LOGIN_FORM_URL);
		return loginHtml;
	}

	private String getCSRFToken(String html) {
		Document document = Jsoup.parse(html);
		Elements elements = document.select("input[name='CSRFToken']");
		if(elements.size() <= 0) {
			return null;
		}
		return elements.get(0).val();
	}

	public static void main(String[] args){
		StudentInfo studentInfo = new StudentInfo("G60714482", "qwerty2018", "4", "2953863");
		//StudentInfo studentInfo = new StudentInfo("ES741857412", "hejianok", "6", "2951775");
		EtestImpl etest = new EtestImpl(studentInfo);
		while(true) {
			int result = etest.login();
			if(result != 0) {
				break;
			}
		}

		while(true) {
			try{
				boolean result = etest.book();
				if(result) {
					break;
				}
			} catch (RuntimeException e) {
				main(args);
			}
		}
	}

}
