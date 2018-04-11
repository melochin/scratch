package scratch.model.entity;

import java.io.Serializable;
import java.util.UUID;

public class Brochure implements Serializable {

	private String id;

	private String name;

	private String description;

	public Brochure() {
		this.id = UUID.randomUUID().toString();
	}

	public String getId() {
		return id;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Brochure{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}
