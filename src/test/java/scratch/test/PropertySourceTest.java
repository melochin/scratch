package scratch.test;

import org.junit.Test;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.Assert;
import org.springframework.web.context.support.StandardServletEnvironment;

public class PropertySourceTest extends ContextClass{

	@Test
	public void setPropertySourceTest() {
		StandardEnvironment env = (StandardEnvironment) wac.getEnvironment();
		//设置PropertySource
		env.getPropertySources().addFirst(new PropertySource<String>("test") {
			@Override
			public Object getProperty(String name) {
				if("foo".equals(name)) {
					return "lish";
				}
				return null;
			}
			
		});
		//判断获取的Property是否正确
		Assert.state(env.getProperty("foo").equals("lish"));
		System.out.println(env.getProperty("jdbc.password"));
	}
	
	
	@Test
	public void readProperty(){
		
	}
	
}
