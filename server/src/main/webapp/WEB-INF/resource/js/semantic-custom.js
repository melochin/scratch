/**
 * semantic api
 */
$.fn.api.settings.api = {
	'get dictionary data' : 'dic/parentcode/{code}',
	'update dictionary data' : 'dict/update',
	'validate dictionary code' : 'dict/validate/code',
	'delete dictionary data' : 'dict/delete',
	'validate user data' : '/user/api/validate/username'
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

	const TOKEN_NAME = "_csrf";
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

		if(settings.type == POST) {
            // 获取Token数据
            var csrf = document.getElementById(TOKEN_NAME);
            if(csrf != null) {
                map[TOKEN_NAME] = csrf.value;
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

    // 默认ajax请求为get方法
    if(settings.type == null) {
        settings.type = GET;
    }
	if(settings.type == POST) {
		settings.contentType = "application/x-www-form-urlencoded";
	}
    settings.success = function (data) {
        validate = data.validate;
    }

	// send sync ajax get request
	$.ajax(settings);
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

// 初始化modal事件
function initModals() {

	const DEFAULT_ATTACH_CONTROLERS = [
			'img[data-href]',
			'a[data-href]',
			'.ui.button[data-href]',
            'div[data-href]'
		];

    var attchControlers = [];
    var isModalExist = false;
    var $modal = $(".ui.modal");

    function isNeedCreateModal() {
		return attchControlers.length >0 && isModalExist == false;
    }

    function createModal() {
        var $div = $("<div class=\"ui modal\"></div>");
        $div.appendTo($("body").get(0));
        $modal = $(".ui.modal");
        isModalExist = true;
    }

    function attchEvent() {
        for(var j=0; j<attchControlers.length; j++) {
            $modal.modal('attach events', attchControlers[j]);
        }
    }

    // 判断modal是否存在
    if($modal.length > 0) {
        isModalExist = true;
    }

	// 寻找符合条件的控件
    for(var i=0; i<DEFAULT_ATTACH_CONTROLERS.length; i++) {
        var selector = DEFAULT_ATTACH_CONTROLERS[i];
        if($(selector).length > 0) {
            attchControlers.push(selector);
        }
    }

    if(isNeedCreateModal()) {
        createModal();
	}

    attchEvent();

}

