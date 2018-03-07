Date.prototype.format = function (fmt) {
  var o = {
    "y+": this.getFullYear(),
    "M+": this.getMonth() + 1,                	 //月份
    "d+": this.getDate(),                    	//日
    "h+": this.getHours(),                   	//小时
    "m+": this.getMinutes(),                 	//分
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

function extend(child, parent) {  
    var child = child || {};  
    for(var prop in parent) {  
        child[prop] = parent[prop];  
    }  
    return child;  
}  






var ERROR = "jerror";
var PATTERN = "jpattern";
var SAMEAS = "jsameas";
$(document).ready(function(){
	
	//通用校验部分
	var $validForm = $(".form-valid");
	var $validInput = $("input[" + ERROR + "]");
	
	$validForm.submit(function() {
		var submit = true;
		$validInput.each(function() {
			if(!validEvent(this)) {
				submit = false;
			}
		});
		return submit;
	});
	
	$validInput.change(function() {
		if($(this).val().trim() != "") {
			if(!validEvent(this)) {
				$validForm.attr("onSubmit", "return false;");
			} else {
				$validForm.attr("onSubmit", "");
			}
		}
	}); 
	
	var $modal = $('.modal');

	$('.modal').modal({
	    dismissible: true, // Modal can be dismissed by clicking outside of the modal
	    opacity: .5, // Opacity of modal background
	    inDuration: 300, // Transition in duration
	    outDuration: 200, // Transition out duration
	    startingTop: '4%', // Starting top style attribute
	    endingTop: '10%', // Ending top style attribute
	    ready: function(modal, trigger) { 
	      // Callback for Modal open. Modal and trigger parameters available.
	      var href = $(trigger).attr("data-href");
	      // If href exist, load website
	      if(href != null && href.trim() != "") {
	    	  $(modal).load(href);
	      }
	    },
	    complete: function() {} 
	  }
	);

	
});


//------------------------表单校验--------------------------------------
//form needs class "form-valid"
//input needs attr "jerror"   setting error message
//		 	  attr "jpattern" setting Regex
//			  attr "jsameas"  setting input name that value is equal
//校验事件
function validEvent(input) {
	//获取正则表达式
	var pat = $(input).attr(PATTERN);
	var error = $(input).attr(ERROR);
	var same = $(input).attr(SAMEAS);
	if(pat != null && pat.trim() != "") {
		var reg = new RegExp(pat);
		var val = $(input).val();
		if(val == null) val = "";
		
		console.log("find " + PATTERN + " [" + pat + "]");
		console.log("get value" + " [" + val + "]");
	
		if(!reg.test(val)) {
			console.debug("校验失败");
			setMessage(input , error);
			return false
		}
		console.debug("校验通过");
		setMessage(input , "");
	}
	
	if(same != null && same.trim() != "") {
		var value = $("input[name='" + same +"']").val();
		if(value != $(input).val()) {
			console.debug("校验失败");
			setMessage(input , error);
			return false
		}
		console.debug("校验通过");
		setMessage(input , "");
	}
	return true
}

function setMessage(obj, mess) {
	//先判断next是不是class是不是error
	if($(obj).next().attr("class") == "error") {
		$(obj).next().text(mess);
	} else {
		$(obj).after("<span class=\"error\">" + mess + "</span>");
	}
}

/**
 * 
 */

function loadBiliData(){
	//显示加载的进度条
	$("#progress-modal").modal("toggle");
	//读取数据
	$.ajax({
		url:"/MyTools/scratch",
		async:"false",
		//成功刷新页面
		success:function(result){
			window.location.reload();
		},
		//失败关闭进度条
		error:function(result){
			$("#progress-modal").modal("toggle");
		}
	});
}


function addInput(formId, inputName, inputId){
	console.log(formId)
	$("<input/>", {
		name : inputName,
		id: inputId,
		type: "text"
	}).appendTo($( "#" + formId));
}

//假设传入的是form
function getMaxId(child){
	var children = $(child).parent(".myForm").children("input");
	var max = -1;
	var id = 0;
	for(i=0; i<children.length; i++){
		if($(children[i]).attr("type") == "text" ){
			id = $(children[i]).attr("id");
			if(id >= max){
				max = id;
			}
		}
	}
	console.log(max);
	return ++max;
}

/*---------------------------------------------------------*/
function newInput(child, formId){
	newId = getMaxId(child)
	addInput(formId, "searchKeyWords[" + newId + "].keyword", newId)
}

function createRow(index, object) {
	var row = "<input name = 'searchKeyWords[" + index + "].keyword' value=" +　object.keyword +　" class='form-control'/>" +
	"<input name = 'searchKeyWords[" + index + "].searchId' value=" + object.searchId + " class='form-control'/>"
	return row;
}

//传递tag
function createForm(tag) {
	var form = "<form><div class='form-group'></div></form>"
	var group = $(form).find(".form-group")
	for(var i = 0; i < length; i++) {
		//传递key
		$(group).append(createRow(i, keyword));
	}
}

function build() {
	var tags = "${searchTags }"
	console.log(tags.length);
}
	

/*$(document).ready(function() {
	ReactDOM.render(<Form />, document.getElementById("add-word"));
	event();
});
*/
function event() {

	//输入框（默认只读），点击时可编辑
	$("input.keyword").click(function() {
		$(this).attr("readonly", null);
	});

	//输入框，焦点离开时不可编辑
	$("input.keyword").blur(function() {
		if($(this).val() != "") {
			//提交表单
			$(this).parents(".form-keyword").submit();
		}
		$(this).attr("readonly", "readonly");
		location.reload();
	});

	//规定submit事件
//	$(".form-keyword").submit(submitWordForm($(this)));
	
	//JQUERY事件如果直接传函数，会马上触发事件，无限循环。- -
	//删除关键字
	$("#delete-word").click(function(){
		deleteWord($(this).parents(".form-keyword"))
	});
	
	//新增关键字表单
	$("#add-word-form").click(function(){
		addWordForm($(this).parents(".tag-item")
				.find(".tag-words"))	
	});
	
}

function submitWordForm(form) {
	console.log($(form).serialize());
	$.ajax({
		url: $(form).attr("action") ,
		data: $(form).serialize(),
		type: "post"
	});
}

function addWordForm(element) {
	//外键关联
	var form = '<div class="col-md-12"><div class="row tag-word">' + 
	'<form class="form-keyword" action="tag/word" method="post" onsubmit="return false;">' +
	'<div class="col-md-8"><input class="keyword" name="keyword" value="${words.keyword }" readonly="readonly"/>' + 
	'</div></form></div></div>'
	form = $(element).append(form);
	event();
	var input = $(form).find(".keyword").click();
	$(input).click();
	$(input).focus().select();
}

function deleteWord(form) {
	console.log($(form).serialize());
	$.ajax({
		url: "tag/word/delete" ,
		data: $(form).serialize(),
		type: "post",
		success: function(data) {
			console.log(data);
			location.reload();	
		}
	});
}

//modal初始化
function initModal() {
	
}
