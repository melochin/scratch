$(document).ready(function () {
    lazyLoadImage();
})


// 延迟加载图片
var lazyLoadImage = function() {
    var IMG_SRC = 'data-img';
    var $elements = $('[' + IMG_SRC + ']');

    [].forEach.call($elements, function(element) {
        const imgSrc = element.getAttribute(IMG_SRC);
        if(imgSrc == "") return;
        // 隐藏图片
        $(element).css('display', 'none');
        // 加载图片
        var image = new Image();
        image.src = imgSrc;
        // 加载完成赋值
        image.onload = function () {
            $(element).css('background-image', 'url(' + imgSrc + ')');
            element.removeAttribute(IMG_SRC);
            $(element).fadeIn("0.5s");
        }
    });
}

/**
 *
 * 1. $('.ui.error.message') 存在时，直接赋值
 * 2. 不存在时，创建提示框提示
 * @param mess
 * @param type 'success' 'error'
 */
var message = function(mess, type) {

    // 在存在<div class="ui error message"></div>标签的情况下，赋错误值
    function error(mess, $ele) {
        $ele.html('<ul class="list"><li>' + mess + '</li></ul>');
        $ele.css('display', 'block');
    }

    // 创建提示框告知信息（可能错误，可能一般信息）
    function alert(mess, color) {
        // 创建提示框
        var $box = $(
            `
                <div class="alert" style="display: none;">
                    <div class="ui ${color} message">
                        <p>${mess}</p>    
                    </div>
                </div>
            `
        )
        $('body').append($box);
        $box.fadeIn(200);

        // 设置取消事件
        var cancel = function (event) {
            // 隐藏
            $box.transition('fade');
            $box.unbind('click');
            $box.remove();
        }
        $box.bind('click', cancel);
    }


    var color = 'black';
    var ERROR_MESSAGE = '.ui.error.message';


    if(type != null && type == 'error') {
        if($(ERROR_MESSAGE).length > 0) {
            error(mess, $(ERROR_MESSAGE));
            return;
        } else {
            color = 'red';
        }
    }

    alert(mess, color);

}

Date.prototype.format = function (fmt) {
    var o = {
        "y+": this.getFullYear(),
        "M+": this.getMonth() + 1,                	 //月份
        "d+": this.getDate(),                    	//日
        "h+": this.getHours(),                   	//小时
        "m+": this.getMinutes(),                 	//分A
        "s+": this.getSeconds(),                 	//秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S+": this.getMilliseconds()             	//毫秒
    };
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)){
            if(k == "y+"){
                fmt = fmt.replace(RegExp.$1, ("" + o[k]).substr(4 - RegExp.$1.length));
            }
            else if(k=="S+"){
                var lens = RegExp.$1.length;
                lens = lens==1?3:lens;
                fmt = fmt.replace(RegExp.$1, ("00" + o[k]).substr(("" + o[k]).length - 1,lens));
            }
            else{
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
            }
        }
    }
    return fmt;
}


var getToken = function() {
    var $token = $("meta[name=X-CSRF-TOKEN]");
    var token = "";
    if($token == null || $token.length == 0) {
        console.warn("cant find meta[name=X-CSRF-TOKEN], ajax post will failed");
        return null;
    }
        token = $token.get(0).content;

    return {"X-CSRF-TOKEN" : token};
}

var Ajax = {

    getDefaultAjaxSetting : function () {
      var setting = new Object();
      setting.headers = getToken();
      setting.async = false;
      setting.contentType = "application/json; charset=utf-8";
      setting.error = function (jqXHR, textStatus, errorThrown) {
          console.error(jqXHR.responseJSON.error);
          message(jqXHR.responseJSON.error, 'error');
      }
      return setting;
    },

    handleAjaxSetting : function (setting) {
      var defaultSetting = this.getDefaultAjaxSetting();

      if(!setting.hasOwnProperty("headers")) {
          setting.headers = defaultSetting.headers;
      }

      if(!setting.hasOwnProperty("async")) {
          setting.async = defaultSetting.async;
      }

      if(!setting.hasOwnProperty("contentType")) {
          setting.contentType = defaultSetting.contentType;
      }

      if(!setting.hasOwnProperty("error")) {
          setting.error = defaultSetting.error;
      }

      return setting;
    },

    get : function (url, data, ajaxSetting) {
      this.request(url, data, "get", ajaxSetting);
    },

    post : function(url, data, ajaxSetting) {
        this.request(url, data, "post", ajaxSetting);
    },

    put : function(url, data, ajaxSetting) {
        this.request(url, data, "put", ajaxSetting);
    },

    delete : function(url, data, ajaxSetting) {
        this.request(url, data, "delete", ajaxSetting);
    },

    request : function (url, data, method, ajaxSetting) {
        url = CONTEXT + url;
        ajaxSetting = (ajaxSetting == null) ?
            this.getDefaultAjaxSetting() :
            this.handleAjaxSetting(ajaxSetting);

        ajaxSetting.type = method;

        if(data != null) {
            ajaxSetting.data = JSON.stringify(data);
        }

        $.ajax(url, ajaxSetting);
    },

    syncGet : function (url, data) {
        return this.syncRequest(url, data, "get");
    },

    syncPost : function (url, data) {
        return this.syncRequest(url, data, "post");
    },

    syncPut : function (url, data) {
        return this.syncRequest(url, data, "put");
    },

    syncDelete : function (url, data) {
        return this.syncRequest(url, data, "delete");
    },

    syncRequest : function (url, data, method) {
        var success = false;
        url = CONTEXT + url;
        var setting = this.getDefaultAjaxSetting();
        // 考虑data 可能不存在
        if(data != null) {
            var json = JSON.stringify(data);
            setting.data = json;
        }
        // ajax采用同步运行，获取结果值
        setting.async = false;
        setting.type = method;
        setting.success = function (result) {
            success = true;
        }
        setting.error = function (jqXHR, textStatus, errorThrown) {
            console.error(jqXHR.responseJSON.error);
            message(jqXHR.responseJSON.error, 'error');
            success = false;
        }
        $.ajax(url, setting);
        return success;
    }
}
