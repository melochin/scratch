$(document).ready(function() {
	ReactDOM.render(<MultiForm />, document.getElementById("test"));
});

var MultiForm = React.createClass({
	render: function() {
		return (
			<Form keyword = "233" searchId = "1" tagId= "2" />
		);
	}
});
	
var Form = React.createClass({
	render: function() {
	return(
		<div>
			<form className="form-keyword" action="tag/word" method="post" onSubmit={this.handleSubmit}>
				<input className="keyword" name="keyword" value={this.props.keyword} readOnly="readonly" onClick={this.handleClick}/>
				<input type="hidden" name="searchId" value={this.props.searchId} />
				<input type="hidden" name="tag" value={this.props.tagId} />
				<div className="col-md-4 edit">
					<a id="delete-word" href="#">
						<span className="glyphicon glyphicon-trash"></span>
					</a>
				</div>
			</form>
		</div>
	)}, 
	handleSubmit: function() {
		return false;
	},
	handleClick: function() {
		$(".keyword").attr("readOnly", null);
	}
});


