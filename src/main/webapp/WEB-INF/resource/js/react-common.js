$(document).ready(function() {
	initPage();
});

/*
 * <div id="page" data-page="1" data-total="2" data-url="user?p=" ></div>
 * */
function initPage() {
	var $page = $("#page");
	$page.each(function() {
		var cur = $(this).attr("data-page");
		var total = $(this).attr("data-total");
		var url = $(this).attr("data-url");
		loadPage(url, cur, total, this);
	});
}


function loadPage(url, curPage, totalPage, page) {
	ReactDOM.render(<Page url={url} curPage={curPage}  totalPage={totalPage} /> , page);
}

var Page = React.createClass({
	render: function() {
		
		var url = this.props.url;
		var curPage = this.props.curPage;
		var totalPage = this.props.totalPage;
		var left = null;
		var right = null;

		if(curPage >= 1 && curPage <= totalPage) {
			
			if(curPage - 1 > 0) {
				var pre = url + (parseInt(curPage) - 1);
				left = (
					<li>
						<a href={pre} aria-label="Previous"> 
							<span aria-hidden="true">&laquo;</span>
						</a>
					</li>
				);
			}
			
			if(curPage + 1 <= totalPage) {
				var next = url + (parseInt(curPage) + 1);
				right = (
					<li><a href={next} aria-label="Next"> 
						<span aria-hidden="true">&raquo;</span>
					</a></li>
				);
			}
		}
		var url = url + curPage;
		return(
				<ul className="pagination">
					{left}
				<li>
					<a href={url}>{curPage}</a>
				</li>
					{right}
				</ul>
		)
	}
});