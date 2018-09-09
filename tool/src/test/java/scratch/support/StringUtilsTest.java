package scratch.support;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class StringUtilsTest {

	@Test
	public void build() throws Exception {
		String str = "{haha}/{bbb}";
		Assert.assertTrue(StringUtils.build(str, 1, 2).equals("1/2"));
		Assert.assertTrue(StringUtils.build("111/{2}/{3}/11", 1, 2).equals("111/1/2/11"));
		Assert.assertTrue(StringUtils.build("{1}{2}{3}", 1, 2,3).equals("123"));

	}

	@Test
	public void isEmpty() throws Exception {
		String nullStr = null;
		String notNullButEmptyStr = new String();
		String emptryStr = "";
		String notEmptryStr = new String("123");

		Assert.assertTrue(StringUtils.isEmpty(nullStr));
		Assert.assertTrue(StringUtils.isEmpty(emptryStr));
		Assert.assertTrue(StringUtils.isEmpty(notNullButEmptyStr));
		Assert.assertTrue(!StringUtils.isEmpty(notEmptryStr));
	}

}