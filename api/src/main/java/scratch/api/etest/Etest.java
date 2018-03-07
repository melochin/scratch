package scratch.api.etest;

public interface Etest {

	/**
	 * 获取验证码
	 * @return
	 */
	String getCheckImgCode();

	/**
	 * 是否预定
	 * @return
	 */
	boolean book();

	/**
	 * 是否登录
	 * @return
	 */
	int login();
}
