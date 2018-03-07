package scratch.support.service;

import java.util.Map;

public interface TemplateEngine {

	public String getContent(String templateLocation, Map<String, Object> modal);
	
}
