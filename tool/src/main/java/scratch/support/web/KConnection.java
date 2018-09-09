package scratch.support.web;

import java.util.function.Function;
import java.util.stream.Stream;

public class KConnection<T> implements WebConnection {

	private String html;

	private T result;

	private Exception exception;

	public KConnection() {

	}

	protected KConnection(Exception exception) {
		this.exception = exception;
	}

	protected KConnection(String html) {
		this.html = html;
	}

	protected KConnection(String html, T result) {
		this.html = html;
		this.result = result;
	}


	@Override
	public WebConnection url(String url) {
		String text = null;
		try{
			text = new HttpConnection().connect(url);
		} catch (Exception e) {
			return new KConnection(e);
		}
		return new KConnection(text);
	}

	@Override
	public <R> WebConnection success(Function<String, R> function) {
		R result = function.apply(html);
		return new KConnection<>(html, result);
	}

	@Override
	public WebConnection then() {
		if(result != null) {
			if(result instanceof String) {
				String url = (String)result;
				return url(url);
			}
		}
		return null;
	}

	@Override
	public Stream<WebConnection> thenStream() {
		return null;
	}
}
