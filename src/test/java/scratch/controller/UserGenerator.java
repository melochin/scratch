package scratch.controller;

import scratch.model.User;

public class UserGenerator {

	public static User get() {
		User user = new User(new Long(43));
		return user;
	}
}
