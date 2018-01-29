package scratch.model;

public class RedisKey {

	public static final String USER_AUTH_MAIL = "userAuthMail";

	public static final String USER_RESET_PSWD = "userResetPswd";

	private static final String SPLITE = ":";

	public static String redisKey(String key1, String key2) {
		if(key1 != null && key2!= null
				&& !key1.isEmpty() && !key2.isEmpty()) {
			return key1 + ":" + key2;
		}
		return null;
	}

	public static String redisKey(String key1, Long key2) {
		return redisKey(key1, String.valueOf(key2));
	}

}
