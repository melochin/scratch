package scratch.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@javax.persistence.Table(name="manga_page")
public class MangaPage {

	private long seqId;
	
	private long mangaId;
	
	private int pageId;
	
	private String url;
	
	private int status;
	
	public MangaPage(){}
	

	public MangaPage(long mangaId, int pageId, String url) {
		this.mangaId = mangaId;
		this.pageId = pageId;
		this.url = url;
		this.status = 0;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="seqId")
	public long getSeqId() {
		return seqId;
	}

	public void setSeqId(long seqId) {
		this.seqId = seqId;
	}
	
	@Column(name="mangaId")
	public long getMangaId() {
		return mangaId;
	}

	public void setMangaId(long mangaId) {
		this.mangaId = mangaId;
	}

	@Column(name="pageId")
	public int getPageId() {
		return pageId;
	}

	public void setPageId(int pageId) {
		this.pageId = pageId;
	}

	@Column(name="url", length=500)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name="status")
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
