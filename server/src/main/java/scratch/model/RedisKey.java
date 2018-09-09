package scratch.model;

public class RedisKey {

	public static final String USER_AUTH_MAIL = "userAuthMail";

	public static final String USER_RESET_PSWD = "userResetPswd";

	public static final String SEARCH_HISTORY = "searchHistory";

	public static final String DIC = "dictionary";

	public static final String ANIME_COMMENTS = "animeComments";

	public static final String BROCHURES = "brochures";

	public static final String CARDS = "cards";

	public static final String EPISODES = "episodes";

	public static final String RUN_TIME = "runtime";

	public static final String searchHistory(String userId) {
		return SEARCH_HISTORY + ":" + userId;
	}

	public static final String animeCommnets(String animeId) {
		return ANIME_COMMENTS + ":" + animeId;
	}

	public static final String animeCommnets(String animeId, String episodeId) {
		return ANIME_COMMENTS + ":" + animeId + ":" + episodeId;
	}

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

	public static final String episodeHot() {
		return EPISODES + ":hot";
	}

	public static final String episodeIp() {
		return EPISODES + ":ip";
	}

}
