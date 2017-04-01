$("document").ready(function() {
	
	var tag = getUrlParameter("tag");
	console.debug("tag" + tag);
	var $videoList = $("#video_list");
	var $tagWithVideoList = $("#tag_video_list");
	var $biliTypeSetting = $("#bili_type_setting");
	
	console.debug("video_list:" + $videoList.length);
	console.debug("tag_video_list:" + $tagWithVideoList.length);
	
	//判断DOM是否存在，存在即渲染对应组件
	if($videoList.length > 0 ) {
		console.debug("video_list:存在，开始渲染组件");
		getVideoList($videoList.get(0), tag);
	}
	
	if($tagWithVideoList.length > 0 ) {
		console.debug("tag_video_list:存在，开始渲染组件");
		getTagWithVideoList($tagWithVideoList.get(0));
	}
	
	getScratchSetting($biliTypeSetting.get(0));
	
	ReactDOM.render(<Search  />, document.getElementById("search"));
})
//----------------------------------------------------------------------------------------------
var Filter = React.createClass({
	click: function(e) {
		this.props.filter(e, {order: "createDate"});
	},
	render: function() {
		return(
			<div className="row">
				<h5>
					<label>排序：</label>
					<a onClick={this.click}>上传时间排序</a>
				</h5>
			</div>
		)
	}
});

/**
 * Search组件
 * 1.提供视频搜索功能
 */
var Search = React.createClass({
	getInitialState: function() {
		this.params = {keyword:"", order:""};
		return null;
	},
	componentDidMount: function() {
		var _this = this;
		$(window).bind("popstate", function(){
			console.debug(history.state);
			_this.redenrVideos(history.state);
		});
		if(history.state != null) {
			_this.redenrVideos(history.state);	
		}
	},
	getData: function() {
		var _this = this;
		var url = "search?" + $.param(this.params);	//显示的URL
		var requestUrl = "ajax/" + url;				//真正请求的URL
		$.getJSON(requestUrl, function(data){
			//History中插入URL，同时插入
			history.pushState(data, document.title, url);
			console.debug(data);
			_this.redenrVideos(data);
		});
	},
	redenrVideos: function(data) {
		var $videos = $("#videos");
		if($videos.length > 0) {
			ReactDOM.render(
				<div>
					<Filter filter={this.formClick}/> 
					<Videos data={data}/>
				</div>, 
			$videos.get(0));
		}		
	},
	formClick: function(e, map) {
		console.log(map);
		//阻止submit事件
		e.preventDefault();
		console.log(this.params);
		//设置关键字
		var keyword = encodeURIComponent($(this.refs.input).val(), "UTF-8");
		this.params["keyword"] = keyword;
		var _this = this;
		if(map != null) {
			if(map["order"] != null) {
				_this.params["order"] = map["order"];
			}
		}
		//AJAX请求数据
		this.getData();
	},
	
	render: function(){
		return(
			<div ref="box" onFocus={this.inputFocus} onBlur={this.inputBlur}>
				<form ref="form" className="form-inline" onSubmit={this.formClick}>
					<input ref="input" type="text" className="form-control" placeholder="Search" ></input>
					<button type="submit" className="btn btn-default">
						<span className="glyphicon glyphicon-search" ></span>
					</button>
				</form>
			</div>
		)
	}
	
});




//-----------------Scratch运行情况-----------------------------------------------------------------
function getScratchSetting(element) {
	var url = "../bili/ajax/getVideoTypesCount";
		$.getJSON(url, function(data) {
			console.log(data);
			ReactDOM.render(<ScratchProgressList types={data.types} currentCounts={data.currentCount} />, element);
		});			
}

var ScratchProgressList = React.createClass({
	
	//初始化状态
	getInitialState: function() {
		
		this.timer = null;
		this.info = "1";
		return {isRun:false, types:this.props.types,
				currentCounts:this.props.currentCounts, result:"" }
	},
	componentDidMount: function() {
		this.ajaxRunStatus(true);
	},
	
	ajaxRunStatus:function(init) {
		//发送AJAX请求，判断是否处于运行状态
		var url = "../bili/ajax/isRun";
		var _this = this;
		$.getJSON(url, function(data) {
			console.log(data);
			if(data.run == false && init != true) {
				//设置运行结果
				var record = data.record;
				var message = (record.error != null ?
						"异常：" + record.error : 
						"共抓取数据:" + record.scratchCount + "项" + 
						";耗时" + (record.endTime - record.startTime)/1000 + "秒");
				_this.setState({result : message});
			}
			_this.setRunStatus(data.run);
		});	
	},
	refreshRun:function() {
		this.ajaxRunStatus(false);
		var url = "../bili/ajax/getVideoTypesCount";
		var _this = this;
		$.getJSON(url, function(data) {
			console.log(data);
			_this.setState({types: data.types, currentCounts: data.currentCount})
		});
	},
	setRunStatus: function(status) {
		if(this.state.isRun == status) return;
		//如果设置成运行中
		//则要定时刷新运行状态和数量
		if(status == true) {
			this.timer = setInterval(this.refreshRun, 5000);
		} else {
			clearInterval(this.timer);
		}
		this.setState({isRun:status});
	},
	btnClick: function(event) {
		if(this.state.isRun == false) {
			//启动爬虫服务
			var url = "../bili/ajax/startService";
			$.getJSON(url);
			console.log("执行");
			this.setRunStatus(true);
		} else {
			//停止运行
			this.setRunStatus(false);
		}
	},
	render:function() {
		var types = this.state.types;
		var currentCounts = this.state.currentCounts;
		var items = types.map(function(type, index){
			return(
				<div className="row">
					<div className="row">
						<div className="col-md-2">
							<h3>{type.name}</h3>
						</div>
					</div>
					<ScratchProgressChildList types={type.childTypes} 
						currentCounts={currentCounts} />
				</div>
			)
		});
		var btn;
		if(this.state.isRun) {
			btn = <button className="btn btn-primary">正在运行</button>
		} else {
			btn = <button className="btn btn-default" onClick={this.btnClick} >开始运行</button>
		}
		return (
			<div style={{margin: "10px"}}>
				<div className="row">
					<div className="col-md-2">
						{btn}
					</div>
					<div className="col-md-8">
						<span>{this.state.result}</span>
					</div>
				</div>
				{items}
			</div>
		)
	}
})

var ScratchProgressChildList = React.createClass({
	render:function() {
		var currentCounts = this.props.currentCounts;
		var types = this.props.types;
		var items = types.map(function(type, index) {
			var cc = currentCounts[type.code];	//获取已经抓取的数据
			var count = type.videoCount;
			var percent = cc / count * 100;
			return <ScratchProgress type={type.name} percent={percent}/>
		});
		return (
			<div>
				{items}
			</div>
		)
	}
})

var ScratchProgress = React.createClass({
	render:function() {
		var style = {width: this.props.percent + "%"};
		return(
			<div className="row">
				<div className="col-md-2">
					<span>{this.props.type}</span>
				</div>
				<div className="col-md-8">
					<div className="progress" >
					  <div className="progress-bar progress-bar-striped active" role="progressbar" aria-valuenow="45" aria-valuemin="0" aria-valuemax="100" style={style}>
					    <span className="sr-only">{this.props.percent + "%"} Complete</span>
					  </div>
					</div>
				</div>
			</div>
		)
	}
})


//----------------------------------------------------------------------------------------------
var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : sParameterName[1];
        }
    }
};
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
	
	jumpEvent: function() {
		window.location.href = "info?tag=" + this.props.tag.tagId;
	},
	
	render:function() {
		return (
			<div className="row">
				<div className="col-md-9">
					<h1>{this.props.tag.tagName}</h1>
				</div>
				<div className="col-md-3"> 
					<button className="btn btn-default" onClick={this.clickEvent}>更新</button>
					<button className="btn btn-default" onClick={this.jumpEvent}>更多</button>
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
		var items = null;
		if(info != null) {
			items = info.map(function(item, index) {
				return <Video key={index} url={item.url} pic={item.picUrl} shortName={item.title} uploader={item.uploader} />
			}); 
		}
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
					<div className="row" >
						<div className="col-md-12" >
							<h6>上传者: {this.props.uploader}</h6>
						</div>
					</div>
				</div>
			</div>		
		)
	}
});
