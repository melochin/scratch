
function loadPage(url, curPage, totalPage) {
	ReactDOM.render(<Page url={url} curPage={curPage}  totalPage={totalPage} /> , $("#page").get(0));
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
				var url = url + (curPage - 1);
				left = (
					<li>
						<a href={url} aria-label="Previous"> 
							<span aria-hidden="true">&laquo;</span>
						</a>
					</li>
				);
			}
			
			if(curPage + 1 > 0) {
				var url = url + (curPage + 1);
				right = (
					<li><a href={url} aria-label="Next"> 
						<span aria-hidden="true">&raquo;</span>
					</a></li>
				);
			}
		}
		var url = url + curPage;
		return(
			<nav aria-label="Page navigation">
				<ul className="pagination">
					{left}
				<li>
					<a href={url}>{curPage}</a>
				</li>
					{right}
				</ul>
			</nav>		
		)
	}
});