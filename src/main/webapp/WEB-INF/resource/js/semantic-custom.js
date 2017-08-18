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
$.fn.form.settings.prompt.email = '{name}不符合邮箱地址规则';
$.fn.form.settings.prompt.url = '{name}不符合链接规则';


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
 * 组件初始化
 */
$(document).ready(function() {
	// 下拉表单初始化
	$('.ui.dropdown')
		.dropdown();	
	// button click 触发modal
	if($(".ui.button[data-href]").length > 0) {
		$(".ui.modal").modal('attach events', '.ui.button[data-href]');
	}
	if($("img[data-href]").length > 0) {
		$(".ui.modal").modal('attach events', 'img[data-href]');
	}
	if($("a[data-href]").length > 0) {
		$(".ui.modal").modal('attach events', 'a[data-href]');
	}
	$('.special.cards .image').dimmer({
		  on: 'hover'
	});
	
});

