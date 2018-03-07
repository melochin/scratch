package scratch.support.service;

import java.util.Locale;
import java.util.Map;

import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

public class ThymeleafTempalte implements TemplateEngine {

	private SpringTemplateEngine springTemplateEngine;

	public void setSpringTemplateEngine(SpringTemplateEngine springTemplateEngine) {
		this.springTemplateEngine = springTemplateEngine;
	}

	@Override
	public String getContent(String templateLocation, Map<String, Object> modal) {
		return springTemplateEngine.process(templateLocation, new Context(Locale.CHINA, modal));
	}

}
