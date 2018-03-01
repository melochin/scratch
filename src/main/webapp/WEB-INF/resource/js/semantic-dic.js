
$(document).ready(function() {

	var rows = (
		<tr>
			<td>1</td>
		<td>2</td>
		</tr>
	)

	ReactDOM.render(<EditForm titles={["a","b"]} rows={rows}/>, document.getElementById("test-container"));

	var $table = $('.ui.ten.wide.column');

	var $items = $(".dict.item");

    $items.api({
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
	

//　渲染加载
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
/*
* 参数：
* List<Dict> data
* String code
* */
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

	// 显示数据编辑窗口　设置当前编辑的dict
	handleModifiedClickEvent : function(dict) {
		this.setState({currentDic:dict});
		var $modal = $('.add.modal');
		$modal.modal('show');
	},

	// 显示删除提示窗口　
	handleDeleteClickEvent : function(dict) {
		this.setState({currentDic:dict});
		var $modal = $('.basic.delete.modal');
		$modal.modal('show');
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

	getTrs : function () {
        var _this = this;
        var trs;
        if(this.props.dicts != null) {
            trs = this.props.dicts.map(function(dict, index) {
                return (
                    <Tr code={dict.code} value={dict.value} sequence={dict.sequence} used={dict.used}
                		modifyClick = {_this.handleModifiedClickEvent.bind(_this, dict)}
                		deleteClick={_this.handleDeleteClickEvent.bind(_this, dict)}/>
            	)
            });
        }
        return trs;
    },

	render: function() {
		var _this = this;
		var trs = _this.getTrs();
		
		var form = <DidctForm dict={this.state.currentDic}/>;

		return(
			<div>
				<div>
					<button id="addDictionaryData" className="ui teal button" onClick={this.handleClick}>
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
			dict = {
				code : "",
				parentCode : "",
				value : "",
                sequence : "",
                used : "",
			}
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
		
		if(nextProps.dict !== this.state.dict) {
			this.updateState(nextProps.dict);
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

var EditForm = React.createClass({

	getColumnTitles : function() {
        return this.props.titles.map(function (title, index) {
            return <th className="single line">{title}</th>
        });
	},

	getRows : function() {
		var _this = this;
		return this.props.rows;
	},

	render : function () {
		// 动态生成列标题
		var columnTitles = this.getColumnTitles();
		var rows = this.getRows();
		return (
			<div>
				{this.props.head}
				<table className="ui celled padded table">
					<thead>
						<tr>
							{columnTitles}
						</tr>
					</thead>
					<tbody>
						{rows}
					</tbody>
            	</table>
			</div>
    	)
    }
})

