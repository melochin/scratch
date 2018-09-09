package scratch.support.service;

public class Page {

	private Integer current;		// 当前页

	private Integer total;			// 总页

	private Integer size;			// 一页所含最大的记录数

	private Long totalItems;		// 总记录数

	public Page() {}
	
	public Page(Integer current) {
		this(current, 20);
	}
	
	public Page(Integer current, Integer size) {
		this.current = current;
		this.size = size;
	}


	public Page(com.github.pagehelper.Page pagehelperPage) {
		this.current = pagehelperPage.getPageNum();
		this.total = pagehelperPage.getPages();
		this.size = pagehelperPage.getPageSize();
		this.totalItems = pagehelperPage.getTotal();
	}

	public Integer getCurrent() {
		return current;
	}

	public void setCurrent(Integer current) {
		this.current = current;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Long getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(Long totalItems) {
		this.totalItems = totalItems;
	}



}
