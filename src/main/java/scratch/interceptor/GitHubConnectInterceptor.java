package scratch.interceptor;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.web.ConnectInterceptor;
import org.springframework.social.github.api.GitHub;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

import scratch.model.User;
import scratch.support.web.SessionSupport;

public class GitHubConnectInterceptor implements ConnectInterceptor<GitHub>{

	@Override
	public void preConnect(ConnectionFactory<GitHub> connectionFactory, MultiValueMap<String, String> parameters,
			WebRequest request) {
		return ;
	}

	/**
	 * Social Connection完毕后，获取用户信息，存入Session 
	 */
	@Override
	public void postConnect(Connection<GitHub> connection, WebRequest request) {
		ConnectionData data = connection.createData();
		User user = new User(data.getDisplayName());
		user.setStatus("1");
		SessionSupport.setUser(user);
	}

}
