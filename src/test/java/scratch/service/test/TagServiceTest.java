package scratch.service.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import scratch.model.SearchKeyword;
import scratch.model.SearchTag;
import scratch.service.SearchTagService;
import scratch.test.ContextClass;

public class TagServiceTest extends ContextClass{
	
	private static Logger log = Logger.getLogger(TagServiceTest.class);
	
	@Autowired
	SearchTagService service;
/*
	@Transactional
	@Test
	public void insertTest() {
		//Tag±£¥Êµ•∂¿≤‚ ‘
		System.out.println("Save Test");
		SearchTag tag = new SearchTag("·∞");
		service.update(tag);
	}
	
	@Test
	public void insertNullTest() {
		System.out.println("Save NULL Test");
		SearchTag tag = new SearchTag("·∞");
		tag.setTagName(null);
		service.update(tag);
	}		
	
	@Test
	public void updateTest() {
		System.out.println("Update Test");
		SearchTag tag = new SearchTag("·∞");
		service.update(tag);
		tag.setTagName("K");
		service.update(tag);
	}
	
	@Test
	public void addListTest() {
		System.out.println("Add List Test");
		SearchTag tag = new SearchTag("EE");
		tag.addKeyword(new SearchKeyword("CC"));
		service.update(tag);
	}
	*/
}
