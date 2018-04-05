package scratch.model.entity;

import java.io.Serializable;
import java.util.UUID;

public class Card implements Serializable {

	private final static long serialVersionUID = -7305157881878994897L;

	private String id;

	private String key;

	private String value;

	public Card(){}

	public Card(String key, String value) {
		this.key = key;
		this.value = value;
		this.id = UUID.randomUUID().toString();
	}

	public void generateId() {
		this.id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Card card = (Card) o;

		return id != null ? id.equals(card.id) : card.id == null;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
