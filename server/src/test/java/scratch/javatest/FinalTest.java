package scratch.javatest;

import org.junit.Test;

public class FinalTest {

	private int time = 1000000000;

	private boolean isEven(int even) {
		return even % 2 == 0;
	}

	private final boolean isEvenFinal(int even) {
		return even % 2 == 0;
	}

	@Test
	public void finalTest() {
		long currentTime = System.currentTimeMillis();
		for(int j=0; j<time; j++) {
			for(int i=0; i<time; i++) {
				isEvenFinal(i);
			}
		}
		System.out.println((System.currentTimeMillis() - currentTime));
	}

	@Test
	public void test() {
		long currentTime = System.currentTimeMillis();
		for(int j=0; j<time; j++) {
			for(int i=0; i<time; i++) {
				isEven(i);
			}
		}
		System.out.println((System.currentTimeMillis() - currentTime));
	}

}
