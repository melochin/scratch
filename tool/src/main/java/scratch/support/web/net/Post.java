package scratch.support.web.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Post extends Connector{

	private final static String POST = "POST";

	public Post(URL url) {
		super(url);
	}

	public HttpURLConnection open(URL url) throws IOException {
		return (HttpURLConnection) url.openConnection();
	}

	@Override
	public HttpURLConnection doConnect(HttpURLConnection connection) throws Exception {

		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod(POST);
		connection.connect();

		writeParam(connection);

		return connection;
	}

	private void writeParam(HttpURLConnection connection) throws IOException {
		String param = requestParam.toString();
		OutputStream output = connection.getOutputStream();
		output.write(param.getBytes("UTF-8"));
		output.flush();
		output.close();
	}

}
