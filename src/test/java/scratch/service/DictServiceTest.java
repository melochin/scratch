package scratch.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import scratch.model.entity.Dict;
import scratch.test.ContextTest;

@Rollback
@Transactional(propagation=Propagation.REQUIRES_NEW)
public class DictServiceTest extends ContextTest {
	
	@Autowired
	private DictService service;

	private Dict generateDict() {
		Dict dict = new Dict();
		dict.setParentCode("-1");
		dict.setCode("-1");
		dict.setValue("test");
		return dict;
	}
	
	@Test
	public void save() {
		Dict dict = generateDict();
		Assert.assertTrue("dictionary save fail", service.save(dict) == 1);
		
		int result = 0;
		try {
			result = service.save(dict);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Assert.assertTrue("dictionary duplicate save", result == 0);
		}
	}
	
	@Test
	public void update() {
		Dict dict = generateDict();
		service.save(dict);
		dict.setSequence(1);
		Assert.assertTrue("dictionary update fail", service.update(dict) == 1);
	}
	
	@Test
	public void delete() {
		Dict dict = generateDict();
		service.save(dict);
		Assert.assertTrue("dictionary delete fail", service.delete(dict) == 1);
	}
	
}
