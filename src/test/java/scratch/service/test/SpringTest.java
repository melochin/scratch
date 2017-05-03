package scratch.service.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.core.Conventions;

import scratch.test.ContextTest;

public class SpringTest extends ContextTest{

	@Test
	public void test() {
		Integer[] ints = new Integer[] {};
		Integer num = new Integer(1);
		List<Integer> numList = new ArrayList<Integer>();
		
		System.out.println(Conventions.getVariableName(num));
		System.out.println(Conventions.getVariableName(ints));
		System.out.println(Conventions.getVariableName(numList));
	}
	
}
