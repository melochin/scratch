package scratch.controller.home;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.UserIdSource;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class SocialController extends ConnectController{
	
	private static final Logger log = Logger.getLogger(SocialController.class);
	
	@Autowired
	private UsersConnectionRepository userConnectionRepository;
	
	@Autowired
	private UserIdSource userIdSource;
	
	public SocialController(ConnectionFactoryLocator connectionFactoryLocator,
			ConnectionRepository connectionRepository) {
		super(connectionFactoryLocator, connectionRepository);
	}
	
	@RequestMapping(value="/{providerId}", method=RequestMethod.POST)
	public RedirectView connect(@PathVariable String providerId, NativeWebRequest request) {
		//判定是否已经存在Connection
		ConnectionRepository connectionRepository = 
				userConnectionRepository.createConnectionRepository(userIdSource.getUserId());
		List<Connection<?>> connections = connectionRepository.findConnections(providerId);
		
		if(connections.isEmpty()) {
			
			log.debug("不存在Connection，重定向授权认证地址");	
			
			return super.connect(providerId, request);
		}
		
		log.debug("已经存在Connection");
		
		return connectionStatusRedirect(providerId, request);
	}
	
	
}
