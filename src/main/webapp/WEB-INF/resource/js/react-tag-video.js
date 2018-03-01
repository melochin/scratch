$("document").ready(function() {
	
	var $biliTypeSetting = $("#bili_type_setting");
	
	if($(".video-type").length > 0) {
		var videos = $(".video-type").get(0);
		var id = $(videos).attr("id");
		var params = {type : id, page : 1};
		console.debug(params);
		ReactDOM.render(<VideoList  params={params} ajax={AJAX.getVideos}/>, videos);
	} 
	
	if($biliTypeSetting.length > 0) {
		getScratchSetting($biliTypeSetting.get(0));
	}
	
	/*ReactDOM.render(<Search  />, document.getElementById("search"));*/
	
	loadFollowViodes();
	
	loadHotVideos();

});


function loadFollowViodes() {
	var $videos = $("#videos-follow");
	if($videos.length > 0) {
		var params = {};
		AJAX.getFollowVideos(params, function(data){
			ReactDOM.render(<VideoList  params={params} ajax={AJAX.getFollowVideos} />, $videos.get(0));
		});
	}
		
	//可能被绑定过了
	$(".tag").find("a").unbind('click').click(function() {
		var params = {tag : $(this).attr("id")};
		ReactDOM.render(<Loading />, $videos.get(0));	
		AJAX.getFollowVideos(params, function(data){
			ReactDOM.render(<VideoList  params={params} ajax={AJAX.getFollowVideos} />, $videos.get(0));
		});
	})		
}

function loadHotVideos($element) {
	var $followHot = $(".video-section #follow");
	var $typeHot = $(".video-section .videos-type");
		
	if($followHot.length > 0) {
		var params = {}
		AJAX.getFollowVideos(params, function(data){
			ReactDOM.render(<Videos  data={data.data} max={"12"} cols={"6"}/>, $followHot.get(0));
		});
	}
		
	if($typeHot.length > 0) {
		$typeHot.each(function(){
			var videoType = $(this).attr("id");
			var params = {keyword:"", type : videoType  , order : "play"};
			var _this = this;
			AJAX.getVideos(params, function(data){
				console.debug(data);
				ReactDOM.render(<Videos  data={data.data} max={"12"} cols={"6"}/>, _this);
			});
		});
	}
}

var AJAX = class {

	static getVideos(params, callback) {
		var url = "/scratch/ajax/search";
		AJAX.getJSON(url, params, callback);
	}


	static getFollowVideos(params, callback) {
		var url = "/scratch/ajax/follow/videos";
		AJAX.getJSON(url, params, callback);
	}

	static getRunStatus(params, callback) {
		var url = "/scratch/bili/ajax/isRun";
		AJAX.getJSON(url, params, callback);
	}

	static getVideoTypesCount(callback) {
		var url = "/scratch/bili/ajax/getVideoTypesCount";
		AJAX.getJSON(url, null, callback);
	}

	static startService(callback) {
		var url = "/scratch/bili/ajax/startService";
		AJAX.getJSON(url, null, callback);
	}

	static getJSON(url, params, callback) {
		if(params != null) {
			var url = url + "?" + $.param(params);
		}
		$.getJSON(url, function(data){
			console.debug("request url:" + url);
			console.debug("response data:");
			console.debug(data);
			callback(data);
		});
	}
};

var Loading = React.createClass({
	render: function() {
		return (
			<div class="spinner">
				<div class="double-bounce1"></div>
				<div class="double-bounce2"></div>
	 		</div>
		)
	}
});

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
		AJAX.getVideos(this.params, function(data){
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
	AJAX.getVideoTypesCount(function(data) {
		ReactDOM.render(<ScratchProgressList types={data.types} currentCounts={data.currentCount} />, element);
	})
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
		var _this = this;
		AJAX.getVideoTypesCount(function(data){
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
			AJAX.startService(function(data){
				console.log("执行");
			});
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

/**
 * 渲染复数的Video组件
 * 不具有动态读取数据的功能
 * data:传入的JSON数据
 * cols:指定列个数，不传入默认为6
 * max:最大显示数，不指定以传入的数据个数为准
 */
var Videos = React.createClass({
	render:function() {
		var info = this.props.data;
		
		var colNum = 6;
		if(this.props.cols != null) {
			colNum = this.props.cols;
		}
		var colWidth = 12 / colNum;
		
		//决定显示个数
		var length = info.length;
		if(this.props.max != null) {
			if(this.props.max < info.length) {
				length = this.props.max;
			}
		}
		
		var itemRows = [];
		if(info != null) {
			for(var i=0; i<length;) {
				var items = [];
				for(var k=0; k<colNum && i<length; k++, i++) {
					var item = info[i];
					items[k] = <Video key={i} url={item.url} pic={item.picUrl} 
							shortName={item.title} uploader={item.uploader} width={colWidth} play={item.play}/>
				}
				itemRows.push(<div className="row">{items}</div>);
			}
		}
		return (
			<div>
				{itemRows}
			</div>	
		)
	}
});

/**
 * VideoList组件
 * 1.document滚动触发AJAX，请求数据，重新渲染组件
 * 2.属性params，决定了请求链接的参数
 * 3.渲染复数Video
 */
var VideoList = React.createClass({

	//初始化状态
	getInitialState: function() {
		this.params = this.props.params;
		this.curPage = 0;
		this.totalPage = 1;
		this.load = false;
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
	//判断能否触发滚动加载
	canScrollLoad: function() {
		//判断滚动条是否到指定位置
		var scrollHight = $(window).scrollTop() + $(window).height();
		var height = $(document).height();
		if(scrollHight < (height * 0.9) && this.state.videoList.length > 0) return false;
		//判断是否正在进行AJAX请求
		if(this.load) return false;
		return true;
	},
	//处理滚动事件
	handleScroll: function() {
		if(!this.canScrollLoad()) return;
		this.load = true;
		//判断页数是否超过总数
		this.curPage++;
		console.debug("curPage:" + this.curPage + "; totalPage:" + this.totalPage);
		if(this.curPage > this.totalPage) return ;
		//更新页数数据
		this.params["page"] = this.curPage;
		var _this = this;
		this.props.ajax(this.params, function(data) {
			_this.load = false;
			_this.totalPage = data.page.totalPage;
			_this.addVideos(data.data);
		});
	},
	
	render:function() {
		var videos = this.state.videoList;
		return (
			<div className="row">
				<Videos  data={videos} cols={"6"}/>
			</div>	
		)
	}
});

/**
 * 组件:视频
 * model:Video
 * props:
 * width 	容器宽度，1-12，指定col-md的宽度
 */
var Video = React.createClass({
	render:function() {
		var width = this.props.width;
		if(width == null) {
			width = "col-md-2";
		} else {
			width = "col-md-" + width;
		}
		var play = this.props.play;
		var unit = "";
		if(play > 1000) {
			play = parseFloat(play /1000).toFixed(1)
			unit = "千";
		}
		if(play > 1000) {
			play = parseFloat(play /1000).toFixed(1)
			unit = "百万";
		}
		play = play + unit;
		
		var uploader = this.props.uploader;
		if(uploader.length > 3) {
			uploader = uploader.substring(0,3) + "..";
		}

		return(
			<div className={width}>
				<div className="thumbnail">
					<div className="video-img" >
						<a href={this.props.url }>
							<img src={this.props.pic } className="img-rounded"/>
						</a>
					</div>
					<div className="video-desc">
						<a href={this.props.url } >{this.props.shortName }</a>
					</div>
					<div className="row" >
						<div className="col-md-6" >
							<h6>
								<span className="glyphicon glyphicon-upload">
									{uploader}
								</span>
							</h6>
						</div>
						<div className="col-md-6">
							<h6>
								<span className="glyphicon glyphicon-play-circle">{play}</span>
							</h6>
						</div>
					</div>
				</div>
			</div>		
		)
	}
});
