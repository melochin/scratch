var content = [ {
	title : 'Horse',
	description : 'An Animal',
}, {
	title : 'Cow',
	description : 'Another Animal',
} ];

$.fn.api.settings.api = {
	'get dictionary data' : 'dic/parentcode/{code}',
};
	
$(document).ready(function() {
	$('.ui.dropdown').dropdown();

	$('.ui.form').form({
		on : 'blur',
		fields : {
			code : 'empty',
			name : 'empty',
			order : 'number'
		}
	});

	$('.ui.search').search({
		source : content,
		searchFields : [ 'title' ]
	});


	var $table = $('.ui.ten.wide.column');
	$('.item').api({
		action : "get dictionary data",
		beforeSend: function(settings) {
			ReactDOM.render(<Loading />, $table.get(0));
			return settings;
		},
		onSuccess : function(data) {
			console.debug(data);
			ReactDOM.render(<Table dicts={data} />, $table.get(0));
			$('#addDictionaryData').click(function() {
				$('.ui.small.modal').modal('show');
			});
		}
	});
	
});

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
	render: function() {
		var trs = this.props.dicts.map(function(dict, index) {
			return (
				<Tr code={dict.code} value={dict.value} sequence={dict.sequence} used={dict.used}/>
			)
		});		
		return(
			<div>
			<div>
				<button id="addDictionaryData" className="ui teal button">
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
						<input type="text" placeholder="文字" value={this.props.value}></input>
					</div>
				</td>
				<td>
					<div className="ui transparent input">
						<input type="text" placeholder="顺序" value={this.props.sequence}></input>
					</div>
				</td>
				<td><select className="ui dropdown" value={this.props.used}>
						<option value="" >状态</option>
						<option value="1">使用</option>
						<option value="0">暂停</option>
				</select></td>
			</tr>		
		)
	}
});

