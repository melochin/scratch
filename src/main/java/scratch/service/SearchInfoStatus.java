package scratch.service;

public enum SearchInfoStatus {

	UNREAD(1), READED(2), UNNEED(9);
	
	private int code;
	
	private SearchInfoStatus(int code){
		this.code = code;
	}
	
}
