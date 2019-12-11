package scratch.model;

public enum DictCode {

	HOST("01", "HOST"),

	ANIME_TYPE("02", "ANIME_TYPE");

	private String code;

	private String info;

	DictCode(String code, String info) {
		this.code = code;
		this.info = info;
	}
}
