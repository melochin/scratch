/**
 * semantic api
 */
$.fn.api.settings.api = {
	'get dictionary data' : 'dic/parentcode/{code}',
	'update dictionary data' : 'dict/update',
	'validate dictionary code' : 'dict/validate/code',
	'delete dictionary data' : 'dict/delete',
	'validate user data' : '/api/validate/username'
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
$.fn.form.settings.prompt.email = '邮箱地址无效';
$.fn.form.settings.prompt.url = '{name}不符合链接规则';


// 新增ajax规则
$.fn.form.settings.rules.ajax = function(value, settings) {

	const POST = "post";
	const GET = "get";

	function setRequestUrl(settings) {
        if (settings.url != null) return;
        if (settings.action != null) {
            settings.url = $.fn.api.settings.api[settings.action];
        }
    }

    function setRequestData($control, settings) {

        var map = {};
        var
            eles = settings.elements,
            keys = (eles == null ? null : Object.keys(eles));

        // get data map
        // default get value from input name and value
        if(keys == null) {
            var control = $control.get(0);
            var key = control.name;
            map[key] = control.value;
        }
        // get value from eles
        else {
            for(var i=0; i<keys.length; i++) {
                var
                    key = keys[i],
                    ele = eles[key],
                    value;
                // ele is jquery object
                if(ele instanceof jQuery) {
                    value = $(ele).val();
                }
                // ele is primitive type
                else {
                    value = ele;
                }
                map[key] = value;
            }
		}

        if(settings.dataType == "json") {
            map = JSON.stringify(map);
        }

        settings.data = map;
    }

    // 设置request url
    setRequestUrl(settings);

	// 设置request data
    setRequestData($(this), settings);

    console.debug(settings.data);

	var validate = false;

    settings.async = false;
    settings.success = function (data) {
        validate = data.validate;
    }

    // 默认ajax请求为get方法
    if(settings.type == null || settings.type == GET) {
        Ajax.get(settings.url, null, settings);
    }
	if(settings.type == POST) {
		settings.contentType = "application/x-www-form-urlencoded";
        Ajax.post(settings.url, null, settings);
	}
	return validate;
};
/**
*	semantic modal settings
*/
$.fn.modal.settings.onShow = function() {
	// 1. 确定modal是否含有data-href,如果有认为是url请求的modal
    console.debug(event);

	var href = $(event.target).attr("data-href");
    console.debug(href);
	var $modal = $(this);

	if(href == null || href == "") return;

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

	$('.special.cards .image').dimmer({
		  on: 'hover'
	});

    initModals();
});

/**
 * 属性：data-href 的标签 添加modal功能 自动加载href页面
 */
function initModals() {

	const selectors = [
			'img[data-href]',
			'a[data-href]',
			'.ui.button[data-href]',
            'div[data-href]'
		];

    var $modal = $(".ui.modal");

    // create modal
    if($modal.length == 0) {
        var $div = $("<div class=\"ui modal\"></div>");
        $div.appendTo($("body").get(0));
        $modal = $div;
    }

    // bind event
    for(var i=0; i<selectors.length; i++) {
        var selector = selectors[i];
        if($(selector).length > 0) {
            $modal.modal('attach events', selector[j]);
        }
    }

}

