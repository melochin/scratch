package scratch.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import scratch.model.SearchKeyword;
import scratch.service.SearchKeywordService;

public class WordServiceTest extends ConfigurationTest{

	@Autowired
	private SearchKeywordService service;
	
/*	@Test
	public void updateKey() {
		SearchKeyword word = new SearchKeyword("2333");
		service.update(word);
	} */
	
}
