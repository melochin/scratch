package scratch.model.view;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import scratch.model.entity.Anime;
import scratch.model.entity.AnimeAlias;

public class AnimeDisplay {

	private Anime anime;

	private boolean focus;

	private Date updateTime;
	
	public AnimeDisplay(Anime anime) {
		super();
		this.anime = anime;
	}

	public Long getId() {
		return anime.getId();
	}

	public String getName() {
		return anime.getName();
	}

	public String getPic() {
		return anime.getPic();
	}

	public Date getPublishMonth() {
		return anime.getPublishMonth();
	}

	public Boolean getFinished() {
		return anime.getFinished();
	}

	public Integer getEpisodeNo() {
		return anime.getEpisodeNo();
	}

	public String getDescription() {
		return anime.getDescription();
	}

	public String getType() {
		return anime.getType();
	}

	public List<AnimeAlias> getAliass() {
		return anime.getAliass();
	}

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public boolean isFocus() {
		return focus;
	}

	public void setFocus(boolean focus) {
		this.focus = focus;
	}


}
