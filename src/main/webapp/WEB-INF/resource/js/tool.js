var getToken = function() {
    var token = $("meta[name=X-CSRF-TOKEN]").get(0).content;
    return {"X-CSRF-TOKEN" : token};
}

var message = function(mess, type) {

    var color = 'black';
    if(type != null && type == 'error') {
        color = 'red';
    };

    $('body').append(
        '<div class="alert">' +
        '<div class="ui ' + color +' message">\n' +
            '        <p>' +　mess  + '</p>\n' +
            '    </div>' +
        '</div>'
    );

    var cancel = function () {
        $('.alert')
            .find('.message')
            .transition('fade');
        $('.alert').remove();
        $(document).unbind('click');
    }

    $(document).bind('click', cancel);
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


var Ajax = {

    getDefaultAjaxSetting : function () {
      var setting = new Object();
      setting.headers = getToken();
      setting.async = false;
      setting.contentType = "application/json; charset=utf-8";
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
        setting.error = function (result) {
            success = false;
        }
        $.ajax(url, setting);
        return success;
    }
}