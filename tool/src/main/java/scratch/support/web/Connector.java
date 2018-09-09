package scratch.support.web;

import java.net.HttpURLConnection;

public interface Connector {

	void beforeConnect(HttpURLConnection httpURLConnection);

	HttpURLConnection connect(HttpURLConnection httpURLConnection);

	void afterConnect(HttpURLConnection httpURLConnection);

}
