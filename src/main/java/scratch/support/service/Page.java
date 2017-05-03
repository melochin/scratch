package scratch.support.service;

public class Page {

	private Integer curPage;		//��ǰҳ��
	
	private Integer totalPage;		//��ҳ��
	
	private Integer perPageItem;	//ÿ��ҳԪ�ظ���
	
	private Long totalItem;			//�ܼ�¼��
	
	public Page() {}
	
	public Page(Integer curPage) {
		this(curPage, 20);
	}
	
	public Page(Integer curPage, Integer perPageItem) {
		this.curPage = curPage;
		this.perPageItem = perPageItem;
	}
	
	public Integer getCurPage() {
		return curPage;
	}

	public void setCurPage(Integer curPage) {
		this.curPage = curPage;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getPerPageItem() {
		return perPageItem;
	}

	public void setPerPageItem(Integer perPageItem) {
		this.perPageItem = perPageItem;
	}

	public Long getTotalItem() {
		return totalItem;
	}

	public void setTotalItem(Long totalItem) {
		this.totalPage = (int) Math.ceil(new Double(totalItem) / new Double(perPageItem));
		this.totalItem = totalItem;
	}

	public Integer getFirstIndex() {
		return (curPage - 1) * perPageItem;
	}
	
}
