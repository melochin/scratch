package scratch.support;

import com.github.pagehelper.PageRowBounds;

public class PageFactory {

	public static PageRowBounds asList(int page) {
		return asList(page, 10);
	}
	
	private static PageRowBounds asList(int page, int size) {
		return new PageRowBounds((page - 1) * size, size);
	}
	
}
