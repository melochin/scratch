// api
$.fn.api.settings.api = {
	'get dictionary data' : 'dic/parentcode/{code}',
	'update dictionary data' : 'dict/update',
	'validate dictionary code' : 'dict/validate/code',
	'delete dictionary data' : 'dict/delete'
};

$.fn.form.settings.debug = true;
$.fn.form.settings.verbose = true;
$.fn.form.settings.performance = false;

// 校验信息模板设置
$.fn.form.settings.prompt.empty = '{name}不能为空';
$.fn.form.settings.prompt.number = '{name}必须为数字';

$.fn.form.settings.onSuccess = function() {
	console.debug(this);
	return true;
}

// 校验规则设置
$.fn.form.settings.rules.ajax = function(value, ajaxValue) {
	var
		action = ajaxValue.action,
		eles = ajaxValue.elements,
		url = $.fn.api.settings.api[action],
		keys = Object.keys(eles),
		map = {};
	
	for(var i=0; i<keys.length; i++) {
		var key = keys[i];
		var value = $(eles[key]).val();
		map[key] = value;
	}
	
	var success = false;
	
	$.ajax({
		url : url, 
		data: map,
		async : false ,
		success: function(data) {
			console.debug(data.validate);
			success = data.validate;
		}
	});
	
	return success;
};

$(document).ready(function() {
	$('.ui.dropdown').dropdown();

	$('#form-dicdata').form({
		on : 'blur',
		inline:true,
		fields : {
			code : {
				rules: [
				  {
					  type : 'empty'
				  }, 
				  {
					  type : 'ajax',
					  value : {
						  action : 'validate dictionary code',
						  elements : {
							  code: $('#form-dicdata').find("input[name='code']"),
							  parentCode: $('#form-dicdata').find("input[name='parentCode']")
						  }
					  },
				  	  prompt : '编码已经存在'
				  }
				 ]
				
			},
			value : {
				rules: [{
					type : 'empty'
				}]
			},
			sequence : {
				rules: [
			        {
			        	type : 'number'
			        }
				]
			}
		}
	});

	var $table = $('.ui.ten.wide.column');
	$('.item').api({
		action : "get dictionary data",
		beforeSend: function(settings) {
			ReactDOM.render(<Loading />, $table.get(0));
			return settings;
		},
		onSuccess : function(data) {
			var _this = this;
			console.debug(data);
			ReactDOM.render(<Table dicts={data.data} code={data.code}  item={_this} />, $table.get(0));
			$(".delete").click(function() {
				
			});
		}
	});
			
	$('#addDictionary').click(function() {
		$('#modal-dic').modal('show');
	});		
			
	$('.ui.search').search({
		source : dictionaries,
		fields: {
			title : 'value'
		},
		searchFields : [ 'value' ],
		onSelect: function(result, response) {
			var code = result.code;
			$('a[data-code="'+code +'"]').click();
		}
	});
	
});

function initModal() {
	$('#modal-dicdata')
	  .modal('attach events', 'addDictionaryData', 'show')
	  .onShow
}	
	
	
var Loading = React.createClass({
	
	render: function() {
		return (
				<div>
				  <div className="ui active loader">Loading</div>
				  <p></p>
				 </div>
		)
	}
});

// <Table dicts={data.data} code={data.code}  item={_this} />

var Table = React.createClass({
	
	getInitialState : function () {
		return {currentDic : ""};
	},
	
	// 刷新重新加载
	refresh : function() {
		this.props.item.click();
	},
	// 删除事件
	handleDeleteEvent : function() {
		var _this = this;
		$.ajax({
			url : $.fn.api.settings.api['delete dictionary data'],
			method : 'post',
			data : {
				code : $('.basic.delete.modal').find(".ok.button").attr("data-code"),
				parentCode : this.props.code
			},
			success : function(data) {
				if(data.success) {
					$('.basic.delete.modal').modal('close');
					_this.refresh();
				} else {
					alert("删除失败");
				}
				
			}
		});
	},
	
	handleModifiedClickEvent : function(dict) {
		this.setState({currentDic:dict});
		var $modal = $('.add.modal');
		$modal.modal('show');
	},
	
	handleDeleteClickEvent : function(dict) {
		this.setState({currentDic:dict});
		var $modal = $('.basic.delete.modal');
		$modal.modal('show');
		/*
		$modal.find(".content").find("p").text("确定删除" + dict.value + "?");
		$modal.find(".ok.button").attr("data-code", dict.code);*/
		
	},
	
	handleClick: function() {
		var code = this.props.code;
		var item = this.props.item;
		var _this = this;
		$('#modal-dicdata').modal({
			onShow: function() {
				$(this).find("input[name='parentCode']").val(code);
			},
			onApprove: function() {
/*				$(this).find("form").submit(function() {
					$.ajax({
						type: $(this).attr("method"),
						url: $(this).attr("action"),
						data: $(this).serialize(),
						success: function(msg) {
							_this.refresh();
						}
					});
					return false;
				});
				$(this).find("form").submit();*/
			},
			onHidden : function() {
				$(this).find("form").form("reset");
			}
		});
		$('#modal-dicdata').modal('show');
	},
	
	render: function() {
		var _this = this;
		var trs;
		
		var form = <DidctForm dict={this.state.currentDic}/>;
		
		if(this.props.dicts != null) {
			trs = this.props.dicts.map(function(dict, index) {
				return (
						<Tr code={dict.code} value={dict.value} sequence={dict.sequence} used={dict.used} 
							modifyClick = {_this.handleModifiedClickEvent.bind(_this, dict)} 
							deleteClick={_this.handleDeleteClickEvent.bind(_this, dict)}/>
					)
				});
		}
		return(
			<div>
			<div>
				<button id="addDictionaryData" className="ui teal button" onClick={this.handleClick.bind(this)}>
					<i className="ui add icon"></i> 字典数据
				</button>
			</div>

			<table className="ui celled padded table">
				<thead>
					<tr>
						<th className="single line">编码</th>
						<th className="single line">文字</th>
						<th className="single line">顺序</th>
						<th className="single line">状态</th>
					</tr>
				</thead>
				<tbody>
					{trs}
				</tbody>
			</table>
			<DeleteModal deleteEvent={this.handleDeleteEvent} dict={this.state.currentDic}/>
			<FormModal form={form} />
			</div>
		)
	}
});

var Tr = React.createClass({
	render: function() {
		return (
			<tr>
				<td>
					<h3>{this.props.code}</h3>
				</td>
				<td className="single line">
					<div className="ui transparent input">
						<input type="text" placeholder="文字" value={this.props.value} name="value"></input>
					</div>
				</td>
				<td>
					<div className="ui transparent input">
						<input type="text" placeholder="顺序" value={this.props.sequence} name="sequence"></input>
					</div>
				</td>
				<td>
					<select className="ui dropdown" value={this.props.used} name="used">
						<option value="" >状态</option>
						<option value="1">使用</option>
						<option value="0">暂停</option>
					</select>
				</td>
				<td>
					<button className="ui button" onClick={this.props.modifyClick} >编辑</button>
					<button className="ui button delete" data-code={this.props.code} onClick={this.props.deleteClick}>删除</button>
				</td>
			</tr>		
		)
	}
});

// header content
var DeleteModal = React.createClass({
	render: function() {
		
		var header = '标题';
		var content = '确定删除';
		
		if(this.props.dict != null) {
			content = '确认删除' + this.props.dict.value;
			header = '数据字典信息';
		}
		
		return (
			<div className="ui small basic delete modal transition hidden" style={{'marginTop' : '-123.5px'}}>
				<div className="ui icon header">
					<i className="warning circle icon"></i>
					{header}
				</div>
				<div className="content">
					<p>{content}</p>
				</div>
			    <div className="actions">
			    	<div className="ui red basic cancel inverted button">
			        	<i className="remove icon"></i>
			        	No
			        </div>
			        <div className="ui green ok inverted button" data-code="" onClick={this.props.deleteEvent}>
			        	<i className="checkmark icon"></i>
			        	Yes
			        </div>
			    </div>
		    </div>
		 );
	}
})

// action data
var FormModal = React.createClass({
	render : function() {
		return(
			<div className="ui small add modal">
				<i className="close icon"></i>
				<div className="header">{this.props.title}</div>
				<div className="content">
					{this.props.form}
				</div>
			</div>
		);
	}
})

// dict action
var DidctForm = React.createClass({
	
	updateState: function(dict) {
		var action;
		if(dict == null) {
			dict = new Object;
			dict.code = "";
			dict.parentCode = "";
			dict.value = "";
			dict.sequence = "";
			dict.used = "";
			action = "add";
		} else {
			action = "update";
		}
		this.setState({dict : dict, action : action});
	},
	
	getInitialState: function() {
		return {dict : "", action : ""};
	},
	componentWillMount : function() {
		this.updateState(this.props.dict);
	},
	componentWillUpdate : function(nextProps, nextState) {
		
		console.debug(this.props.dict);
		console.debug(this.state.dict);
		console.debug(nextProps);
		console.debug(nextState);
		
		if(nextProps.dict !== this.props.dict) {
			this.updateState(this.props.dict);
			return true;
		}
		return false;
	},
	
	handleChange(event) {
		var dic = this.state.dict;
		dic[event.target.name] = event.target.value;
		this.updateState(dic);
	},
	render: function() {
		var dict = this.state.dict;
		var action = this.state.action;
		
		return (
			<form className="ui form" action={action} method="post">
				<div className="ui field fluid">
					<label>编码</label>
					<input name="code" placeholder="编码" value={dict.code} onChange={this.handleChange} type="text" />
					<input name="parentCode" type="hidden" value={dict.parentCode}/>	
				</div>
				<div className="ui field fluid">
					<label>文字</label>
					<input name="value" placeholder="文字" value={dict.value} type="text" />
				</div>	
				<div className="ui field fluid">
					<label>顺序</label>
					<input name="sequence" placeholder="顺序" value={dict.sequence} type="text" />
				</div>	
				<div className="ui field fluid input">
					<select className="ui dropdown" name="used" value={dict.used}>
						<option value="">状态</option>
						<option value="1" selected="selected">使用</option>
						<option value="0">暂停</option>
					</select>
				</div>
				<input className="ui button green submit" type="submit" value="保存"/>
			</form>
		);
	}
})


