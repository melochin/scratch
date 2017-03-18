$("document").ready(function() {
	loadData();
})

function loadData() {
	var url = "/MyTools/json"
	$.getJSON(url, function(data) {
		ReactDOM.render(<TagBox tags={data} />, document.getElementById("videos"));
	});			
}


var TagBox = React.createClass({
	render:function() {
		var tags = this.props.tags;
		var items = tags.map(function(item, index){
			return <VideoBox key={index} item={item} />
		});
		return(
			<div>
				{items}
			</div>
		)
	}
})

var VideoBox = React.createClass({
	render:function() {
		var item = this.props.item;
		var tag = item.searchTag;
		var infos = item.searchInfos;
		return(
			<div>
				<Tag tag={tag} />
				<Videos data={infos} />
			</div>
		)
	}
});

var Tag = React.createClass({
	render:function() {
		return (
			<div className="row">
				<div className="col-md-10">
					<h1>{this.props.tag.tagName}</h1>
				</div>
				<div className="col-md-2"> 
					<button className="btn btn-default" onClick={this.clickEvent}>更新</button>
				</div>
			</div>	
		)
	},
	clickEvent:function(e) {
		var $btn = $(e.currentTarget);
		console.log($btn);
		$btn.attr("disabled", "true");
		var scratchUrl = "scratch/" + this.props.tag.tagId;
		$.ajax({
			url:scratchUrl,
			success: function() {
				loadData();
				$btn.attr("disabled", false);
			},
			fail: function() {
				$btn.attr("disabled", false);
			}
		});
	}
});


var Videos = React.createClass({
	getInitialState: function() {
		return {
			nums:5
		}
	},
	render:function() {
		var info = this.props.data;
		var items = info.map(function(item, index) {
			return <Video key={index} url={item.url} pic={item.pic} shortName={item.shortName} />
		}); 
		console.log(items);
		return (
			<div className="row">
				{items}
			</div>	
		)
	}
});

//video展示控件
var Video = React.createClass({
	render:function() {
		return(
			<div className="col-sm-6 col-md-2">
				<div className="thumbnail">
					<div className="video-img" >
						<a href={this.props.url }>
							<img src={this.props.pic } />
						</a>
					</div>
					<div className="video-desc">
						<a href={this.props.url } >{this.props.shortName }</a>
					</div>
				</div>
			</div>		
		)
	}
});
