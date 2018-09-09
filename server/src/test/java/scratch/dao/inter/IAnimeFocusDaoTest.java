package scratch.dao.inter;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import scratch.test.ContextTest;

import static org.junit.Assert.*;

public class IAnimeFocusDaoTest extends ContextTest{

	@Autowired
	private IAnimeFocusDao focusDao;

	@Test
	public void count() throws Exception {
		focusDao.count(new Long(171));
	}
}