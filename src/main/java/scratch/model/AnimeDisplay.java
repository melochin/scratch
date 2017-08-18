package scratch.model;

import java.util.Date;
import java.util.List;

public class AnimeDisplay {

	private Anime anime;

	private boolean focus;
	
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

	public String getAlias() {
		return anime.getAlias();
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

	public boolean isFocus() {
		return focus;
	}

	public void setFocus(boolean focus) {
		this.focus = focus;
	}
	
}
