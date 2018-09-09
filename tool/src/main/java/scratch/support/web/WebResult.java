package scratch.support.web;

import java.util.function.Function;

public class WebResult<T> {

	private T result;

	protected WebResult(T result) {
		this.result = result;
	}

	protected T get() {
		return result;
	}


}
