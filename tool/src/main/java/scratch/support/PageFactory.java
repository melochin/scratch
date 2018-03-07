package scratch.support;

import com.github.pagehelper.PageRowBounds;

public class PageFactory {

	public static PageRowBounds asList(Integer page) {
		if(page != null && page >0) {
			return asList(page, 10);	
		} else {
			return noLimit();
		}
	}
	
	private static PageRowBounds noLimit() {
		return new PageRowBounds(PageRowBounds.NO_ROW_OFFSET, PageRowBounds.NO_ROW_LIMIT);
	}
	
	private static PageRowBounds asList(int page, int size) {
		return new PageRowBounds((page - 1) * size, size);
	}
	
}
