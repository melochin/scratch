// api
$.fn.api.settings.api = {
	'get dictionary data' : 'dic/parentcode/{code}',
	'update dictionary data' : 'dict/update',
	'validate dictionary code' : 'dict/validate/code'
};

// 校验信息模板设置
$.fn.form.settings.prompt = {
	empty 	: '{name}不能为空',
	number	: '{name}必须为数字'
};

$.fn.form.settings.onSuccess = function() {
	console.debug(this);
	return true;
}

// 校验规则设置
$.fn.form.settings.rules.ajax = function(value, ajaxValue) {
	var
		action = ajaxValue.action,
		eles = ajaxValue.elements,
		prompt = ajaxValue.prompt,
		url = $.fn.api.settings.api[action],
		keys = Object.keys(eles),
		map = {},
		name = $(this).attr("name"),
		$form = $(this).closest(".ui.form");
		
	console.debug(name);
	console.debug($form);
	
	for(var i=0; i<keys.length; i++) {
		var key = keys[i];
		var value = $(eles[key]).val();
		map[key] = value;
	}
	
	console.debug(prompt);
	
	$.ajax({
		url : url, 
		data: map,
		success: function(data) {
			console.debug(data.validate);
			if(!data.validate) {
				$form.form('add prompt', name, prompt);
			}
		}
	});
	
	return true;
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
						  },
						  prompt : '编码已经存在'
					  }
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

var Table = React.createClass({
	
	handleDeleteClick : function() {
		$('.basic.delete.modal').modal('show');
		var code = this.props.code;
		console.debug(code);
	},
	
	handleClick: function() {
		var code = this.props.code;
		var item = this.props.item;
		$('#modal-dicdata').modal({
			onShow: function() {
				$(this).find("input[name='parentCode']").val(code);
			},
			onApprove: function() {
				$(this).find("form").submit(function() {
					$.ajax({
						type: $(this).attr("method"),
						url: $(this).attr("action"),
						data: $(this).serialize(),
						success: function(msg) {
							item.click();
						}
					});
					return false;
				});
				$(this).find("form").submit();
			}
		});
		$('#modal-dicdata').modal('show');
	},
	
	render: function() {
		var _this = this;
		
		var trs = this.props.dicts.map(function(dict, index) {
			return (
				<Tr code={dict.code} value={dict.value} sequence={dict.sequence} used={dict.used} deleteClick={_this.handleDeleteClick}/>
			)
		});		
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
			<DeleteModal />
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
					<button className="ui button">编辑</button>
					<button className="ui button delete" data-code={this.props.code} onClick={this.props.deleteClick}>删除</button>
				</td>
			</tr>		
		)
	}
});

var DeleteModal = React.createClass({
	render: function() {
		return (
			<div className="ui small basic delete modal transition hidden" style={{'marginTop' : '-123.5px'}}>
				<div className="ui icon header">
					<i className="archive icon"></i>
						Archive Old Messages
				</div>
				<div className="content">
					<p>确定删除?</p>
				</div>
			    <div className="actions">
			    	<div className="ui red basic cancel inverted button">
			        	<i className="remove icon"></i>
			        	No
			        </div>
			        <div className="ui green ok inverted button">
			        	<i className="checkmark icon"></i>
			        	Yes
			        </div>
			    </div>
		    </div>
		 );
	}
})
