package scratch.java;

import java.io.Serializable;

public class Per implements Serializable {

	private final static long serialVersionUID = 7921300964532930987L;

	private String name;

	private String sex;

	public Per(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Per{" +
				"name='" + name + '\'' +
				", sex='" + sex + '\'' +
				'}';
	}
}
