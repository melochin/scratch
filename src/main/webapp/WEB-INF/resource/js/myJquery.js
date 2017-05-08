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


