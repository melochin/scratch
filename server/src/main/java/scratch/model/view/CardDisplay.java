package scratch.model.view;

import scratch.model.entity.Card;
import scratch.model.entity.MemoryCardInfo;

import java.util.Date;

public class CardDisplay {

	private Card card;

	private MemoryCardInfo cardInfo;

	public CardDisplay() {
	}

	public CardDisplay(Card card) {
		this.card = card;
		this.cardInfo = new MemoryCardInfo();
	}

	public CardDisplay(Card card, MemoryCardInfo cardInfo) {
		this.card = card;
		this.cardInfo = cardInfo;
	}

	public String getId() {
		return card.getId();
	}

	public String getKey() {
		return card.getKey();
	}

	public void setKey(String key) {
		card.setKey(key);
	}

	public String getValue() {
		return card.getValue();
	}

	public void setValue(String value) {
		card.setValue(value);
	}

	public Integer getRemeber() {
		return cardInfo.getRemeber();
	}

	public void setRemeber(Integer remeber) {
		cardInfo.setRemeber(remeber);
	}

	public Integer getForget() {
		return cardInfo.getForget();
	}

	public void setForget(Integer forget) {
		cardInfo.setForget(forget);
	}

	public Date getLastTime() {
		return cardInfo.getLastTime();
	}

	public void setLastTime(Date lastTime) {
		cardInfo.setLastTime(lastTime);
	}

	@Override
	public String toString() {
		return "CardDisplay{" +
				"card=" + card +
				", cardInfo=" + cardInfo +
				'}';
	}
}
