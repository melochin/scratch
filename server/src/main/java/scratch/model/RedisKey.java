package scratch.model;

public class RedisKey {

	public static final String USER_AUTH_MAIL = "userAuthMail";

	public static final String USER_RESET_PSWD = "userResetPswd";

	public static final String DIC = "dictionary";

	public static final String BROCHURES = "brochures";

	public static final String CARDS = "cards";

	public static final String cards(String brochureId) {
		return "brochure" + ":" + brochureId + ":cards";
	}

	public static final String memory(String brochureId) {
		return "brochure" + ":" + brochureId + ":memory";
	}

	public static final String memoryCards(String brochureId) {
		return memory(brochureId) + ":cards";
	}

	public static final String memoryCardsInfo(String brochureId) {
		return memory(brochureId) + ":cards" + ":info";
	}

}
