$("document").ready(function() {
	
	var $videoList = $("#video_list");
	var $tagWithVideoList = $("#tag_video_list");
	
	console.debug("video_list:" + $videoList.length);
	console.debug("tag_video_list:" + $tagWithVideoList.length);
	
	//判断DOM是否存在，存在即渲染对应组件
	if($videoList.length > 0 ) {
		console.debug("video_list:存在，开始渲染组件");
		getVideoList($videoList.get(0), 0);
	}
	
	if($tagWithVideoList.length > 0 ) {
		console.debug("tag_video_list:存在，开始渲染组件");
		getTagWithVideoList($tagWithVideoList.get(0));
	}
	
})

//含有video和page信息
//数据请求封装在组件中
function getVideoList(element, tag) {
	ReactDOM.render(<VideoList tag={tag}/> , element);
}

//含有tag和对应video信息
//需要主动数据请求调用AJAX，再渲染组件
function getTagWithVideoList(element) {
	var url = "json"
	$.getJSON(url, function(data) {
		ReactDOM.render(<TagWithVideoList data={data}/>, element);
	});			
}

/**
 * TAG与Videos的组合组件复数
 */
var TagWithVideoList = React.createClass({
	render:function() {
		var tags = this.props.data;
		var items = tags.map(function(item, index){
			return <TagWithVideo key={index} item={item} />
		});
		return(
			<div>
				{items}
			</div>
		)
	}
})

/**
 * TAG与Videos的组合组件
 */
var TagWithVideo = React.createClass({
	render:function() {
		var item = this.props.item;
		var tag = item.searchTag;
		var videos = item.searchInfos;
		return(
			<div>
				<Tag tag={tag} />
				<Videos data={videos} />
			</div>
		)
	}
});

/**
 * 渲染Tag组件
 * 1.点击按钮，具有抓取数据的功能
 */
var Tag = React.createClass({
	clickEvent:function(e) {
		var $btn = $(e.currentTarget);
		$btn.attr("disabled", "true");
		var scratchUrl = "scratch/" + this.props.tag.tagId;
		$.ajax({
			url:scratchUrl,
			success: function() {
				loadData();				//！！！应该更改状态重新刷新REACT组件
				$btn.attr("disabled", "false");
			},
			fail: function() {
				$btn.attr("disabled", "false");
			}
		});
	},
	
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
	}
});



/**
 * 渲染复数的Video组件
 * 1.不具有动态读取数据的功能
 */
var Videos = React.createClass({
	render:function() {
		var info = this.props.data;
		var items = info.map(function(item, index) {
			return <Video key={index} url={item.url} pic={item.pic} shortName={item.shortName} />
		}); 
		return (
			<div className="row">
				{items}
			</div>	
		)
	}
});



/**
 * VideoList组件
 * 1.document滚动触发AJAX，请求数据，重新渲染组件
 * 2.属性tag决定要读取的数据
 * 3.渲染复数Video
 */
var curPage = 0;
var totalPage =  1;
var load =  false;

var VideoList = React.createClass({

	//初始化状态
	getInitialState: function() {
		return {videoList:[]}
	},
	
	componentDidMount: function() {
		document.addEventListener('scroll', this.handleScroll);
		//组件装载时，马上触发滚动事件，先请求部分数据
		this.handleScroll();
	},
	
	componentWillUnmount: function() {
		 document.removeEventListener('scroll', this.handleScroll);
	},
	
	//更新状态
	addVideos: function(videos) {
		this.setState({videoList : this.state.videoList.concat(videos)});
	},
	
	handleScroll: function() {
		//判断滚动条是否到指定位置
		var scrollHight = $(window).scrollTop() + $(window).height();
		var height = $(document).height();
		if(scrollHight < (height * 0.9) && this.state.videoList.length > 0) return;
		//判断是否正在进行AJAX请求
		if(load) return;
		load = true;
		//判断页数是否超过总数
		curPage++;
		console.debug("curPage:" + curPage + "; totalPage:" + totalPage);
		if(curPage > totalPage) return ;
		//AJAX请求，获取数据
		var url = "json/info?" + $.param({
			tag: this.props.tag,
			page: curPage
		});
		var _this = this;
		$.getJSON(url, function(pageData) {
			load = false;
			totalPage = pageData.page.totalPage;
			//更新状态
			_this.addVideos(pageData.data);
		});
	},
	
	render:function() {
		var items = this.state.videoList.map(function(item, index) {
			return <Video key={index} url={item.url} pic={item.pic} shortName={item.shortName} />
		}); 
		return (
			<div className="row">
				{items}
			</div>	
		)
	}
});

/**
 * Video组件，对应model为searchInfo
 */
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
