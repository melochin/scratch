/**
 * semantic api
 */
$.fn.api.settings.api = {
	'get dictionary data' : 'dic/parentcode/{code}',
	'update dictionary data' : 'dict/update',
	'validate dictionary code' : 'dict/validate/code',
	'delete dictionary data' : 'dict/delete',
	'validate user data' : 'user/validate'
};

/**
 * seamntic form validate setting
 *
 * */
// debug info
$.fn.form.settings.debug = true;
$.fn.form.settings.verbose = true;
$.fn.form.settings.performance = false;
// 校验信息模板设置
$.fn.form.settings.prompt.empty = '{name}不能为空';
$.fn.form.settings.prompt.number = '{name}必须为数字';
// 新增ajax规则
$.fn.form.settings.rules.ajax = function(value, ajaxValue) {
	console.debug(event);
	var
		action = ajaxValue.action,
		eles = ajaxValue.elements,
		url = $.fn.api.settings.api[action],
		keys = (eles == null ? null : Object.keys(eles)),
		map = {};

	// get data map
	// default get value from input name and value
	if(keys == null && event != null) {
		var key = event.target.name;
		map[key] = event.target.value;
	}
	// get value from eles
	if(keys != null) {
		for(var i=0; i<keys.length; i++) {
			var
				key = keys[i],
				ele = eles[key],
				value;
			// ele is jquery object
			if(ele instanceof JQuery) {
				value = $(ele).val();
			}
			// ele is primitive type
			else {
				value = ele;
			}
			map[key] = value;
		}
	}
	console.debug(map);
	var success = false;
	// send sync ajax get request
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
/**
*	semantic modal settings
*/
$.fn.modal.settings.onShow = function() {
	// 1. 确定modal是否含有data-href,如果有认为是url请求的modal
	console.debug(event);
	console.debug(event.target);
	var href = $(event.target).attr("data-href");
	console.debug(href);
	console.debug(this);
	var $modal = $(this);

	if(href == null || href == "") {
		return;
	}
	console.debug("modal中含有href，进行request请求")
	// 2.ajax同步加载html数据
	// 3.填充到modal
	$.get(href, function(data) {
		console.debug("获取html，填充modal")
		$modal.html(data);
		$modal.modal("refresh");
	})
}
/**
*	具体表单规则的定义
*/
var userRule = {
	on : 'blur',
	inline : true,
	fields : {

		username : {
			rules: [
				{
					type : 'empty'
				},
				{
					type : 'regExp',
					value : '^[a-zA-Z0-9_]{6,18}$',
					prompt : '用户名只能以数字或字母组合，且长度不能少于6位超过18位'
				},
				{
				  type : 'ajax',
				  value : {
					  action : 'validate user data'
				  	},
			  	prompt : '用户名已经存在'
			  }
			]
		},

		email : {
			rules: [{
				type : 'email'
			}]
		},

		password : {
			rules: [
					{
						type : 'empty'
					},
	        {
						type : 'regExp',
						value : '^[a-zA-Z0-9_]{6,18}$',
						prompt : '密码只能以数字或字母组合，且长度不能少于6位超过18位'
	        }
			]
		}

	}
};


var animeRule = {
	on : 'blur',
	inline : true,
	fields : {

		name : {
			rules: [
				{
					type : 'empty'
				}
			]
		},

		type : {
			rules: [{
				type : 'empty'
			}]
		},

		picFile : {
			rules: [{
				type : 'regExp',
				// 条件表达
				value : '\w*(png|jpg)$',
				prompt : '请用.png结尾的图片上传'
			}]
		}

	}
}
