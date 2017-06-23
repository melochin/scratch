package scratch.api.bilibili;

public enum VideoType {

	MUSIC_SYNTHESIZED(27), MUSIC_ORGIGIN(28), MUSIC_3D(29);
	
	private int code;
	
	private VideoType(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
}
