package scratch.support.web.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Get extends Connector{

	public Get(URL url) {
		super(url);
	}

	@Override
	protected HttpURLConnection open(URL url) throws IOException {
		if (requestParam.toString() != null && !requestParam.toString().isEmpty()) {
			url = new URL(url.toString() + "?" + requestParam.toString());
		}
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		return connection;
	}

	@Override
	protected HttpURLConnection doConnect(HttpURLConnection connection) throws Exception {
		connection.setRequestMethod("GET");

		connection.connect();
		return connection;
	}
}
