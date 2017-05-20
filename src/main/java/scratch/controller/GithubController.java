package scratch.controller;

import java.util.List;

import org.springframework.social.connect.Connection;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.github.api.GitHubUser;
import org.springframework.social.github.connect.GitHubConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GithubController {
	
	private static GitHubConnectionFactory connectionFactory;
	
	static {
		connectionFactory = new GitHubConnectionFactory("d241309ae6e8c2d0ba77", "261e556d8fbe7dc0bcd4917efe0cd6b21fccba3a");
	}
	
	@RequestMapping("/github/login")
	public String login() {
		OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
		OAuth2Parameters params = new OAuth2Parameters();
		params.add("scope", "user:follow");
		params.setRedirectUri("http://localhost:8080/scratch/callback-login");
		String authorizeUrl = oauthOperations.buildAuthorizeUrl(params);
		return "redirect:" + authorizeUrl;
	}
	
	@RequestMapping("/callback-login")
	public String handleGithubLogin(@RequestParam("code") String code) {
		OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
		AccessGrant accessGrant = oauthOperations.exchangeForAccess(code, "http://localhost:8080/scratch/callback-login", null);
		Connection<GitHub> connection =  connectionFactory.createConnection(accessGrant);
		String username = connection.getDisplayName();
		GitHub github = connection.getApi();
		List<GitHubUser> users = github.userOperations().getFollowers(username);
		for(GitHubUser u:users) {
			print(u);
		}
		return "";
	}
	
	private void print(GitHubUser user) {
		System.out.println("头像地址：" + user.getAvatarUrl());
		System.out.println("邮箱地址:" + user.getEmail());
		System.out.println("用户姓名：" + user.getName());
		System.out.println("用户ID：" + user.getId());
		System.out.println("登录状态：" + user.getLogin());
		System.out.println("日期：" + user.getDate());
	}
}
