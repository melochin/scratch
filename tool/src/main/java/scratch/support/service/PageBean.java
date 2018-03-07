package scratch.support.service;

import java.util.List;

public class PageBean<T> {

	private List<T> data;
	
	private Page page;
	
	public PageBean(List<T> data, Page page) {
		super();
		this.data = data;
		this.page = page;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

}
