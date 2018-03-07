package scratch.api.renren;

public class VideoEpisode {

	private String num;
	
	private String downloadUrl;

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	@Override
	public String toString() {
		return "VideoEpisode [num=" + num + ", downloadUrl=" + downloadUrl + "]";
	}
	
}
