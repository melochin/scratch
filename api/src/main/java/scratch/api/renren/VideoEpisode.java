package scratch.api.renren;

import java.util.ArrayList;
import java.util.List;

public class VideoEpisode {

	private String num;

	private String season;

	private String downloadUrl;

	private List<String> downloadUrls;

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

	public List<String> getDownloadUrls() {
		return downloadUrls;
	}

	public void addDownloadUrl(String downloadUrl) {
		if (this.downloadUrls == null) {
			this.downloadUrls = new ArrayList<>();
		}
		this.downloadUrls.add(downloadUrl);
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	@Override
	public String toString() {
		return "VideoEpisode{" +
				"num='" + num + '\'' +
				", downloadUrl='" + downloadUrl + '\'' +
				", downloadUrls=" + downloadUrls +
				'}';
	}
}
