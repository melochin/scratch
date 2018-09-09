package scratch.api;

import org.junit.Rule;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.web.servlet.setup.AbstractMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import scratch.test.ContextTest;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

public class DocumentContextTest extends ContextTest {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

	@Override
	protected AbstractMockMvcBuilder doMockMvc() {
		return MockMvcBuilders.webAppContextSetup(wac)
				.apply(documentationConfiguration(this.restDocumentation));
	}
}
