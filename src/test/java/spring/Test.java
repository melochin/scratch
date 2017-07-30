package spring;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import scratch.test.ContextTest;

public class Test extends ContextTest{

	@org.junit.Test
	public void testList() {
		List<String> list = Arrays.asList("1,2,3,4,5,6,1,8".split(","));
		List<String> listWithComma = list.stream()
				.map(str -> str + "1")
				.map(str -> addComma(str))
				.collect(Collectors.toList());
		System.out.println(listWithComma);
		
		System.out.println(Collections.singleton("new-beta-versions-of-concourse-pipeline-and-cloud-foundry-manifest-editing-support-released-for-visual-studio-co"));
	}
	
	public String addComma(String str) {
		return str + ",";
	}
	
}
