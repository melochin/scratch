package scratch.support.web;

import java.util.function.Function;
import java.util.stream.Stream;

public interface WebConnection {

	WebConnection url(String url);

	<R> WebConnection success(Function<String, R> function);

	WebConnection then();

	Stream<WebConnection> thenStream();

}
