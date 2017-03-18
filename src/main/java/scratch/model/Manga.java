package scratch.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="manga_manga")
public class Manga {

	@Id
	@Column(name="mangaId")
	private Long mangaId;
	
	@Column(name="name", length=255)
	private String name;
	
	@Column(name="previewUrl", length=255)
	private String previewUrl;
	
	@Column(name="url", length=255)
	private String url;
	
	public Manga(){}
	
	public Manga(String name, Long mangaId){
		this.name = name;
		this.mangaId = mangaId;
	}
	
	public Manga(Long mangaId, String name, String previewUrl, String url) {
		this.mangaId = mangaId;
		this.name = name;
		this.previewUrl = previewUrl;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getMangaId() {
		return mangaId;
	}

	public void setMangaId(Long mangaId) {
		this.mangaId = mangaId;
	}
	
	public String getPreviewUrl() {
		return previewUrl;
	}

	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		String info = "--------------Manga's info--------------\n";
		info += "Manga's name:" + getName() + "\n";
		info += "Manga's id:" + getMangaId();
		return info;
	}
	
}
