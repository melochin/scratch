package scratch.service;

public class PageBean<T> {

	private T data;
	
	private Page page;
	
	public PageBean(T data, Page page) {
		super();
		this.data = data;
		this.page = page;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

}
