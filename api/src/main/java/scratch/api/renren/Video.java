package scratch.api.renren;

import java.util.Date;

public class Video {

	private String title;
	
	private String imgUrl;
	
	private Date publishAt;
	
	private String resourceUrl;
	
	private String downloadListUrl;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Date getPublishAt() {
		return publishAt;
	}

	public void setPublishAt(Date publishAt) {
		this.publishAt = publishAt;
	}

	public String getResourceUrl() {
		return resourceUrl;
	}

	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}
	
	public String getDownloadListUrl() {
		return downloadListUrl;
	}

	public void setDownloadListUrl(String downloadListUrl) {
		this.downloadListUrl = downloadListUrl;
	}

	@Override
	public String toString() {
		return "Video{" +
				"title='" + title + '\'' +
				", imgUrl='" + imgUrl + '\'' +
				", publishAt=" + publishAt +
				", resourceUrl='" + resourceUrl + '\'' +
				", downloadListUrl='" + downloadListUrl + '\'' +
				'}';
	}
}
