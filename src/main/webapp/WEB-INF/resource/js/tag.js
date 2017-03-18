
$(document).ready(function() {
	//bindEvent();
	Tag.init();
	Word.init();
});

//绑定所有事件
function bindEvent() {


	//规定submit事件
//	$(".form-keyword").submit(submitWordForm($(this)));
	
	
	//新增关键字表单
	$("#add-word-form").click(function(){
		addWordForm($(this).parents(".tag-item")
				.find(".tag-words"))	
	});
	
}

/*--------------------------tag相关事件------------------------------*/


//tag新增事件

//tag编辑事件
var Tag = {
		init: function() {
				
			$(".delete-tag").click(function() {
				Tag.deleteEvent($(this).parents(".tag-item").find(".form-tag"));
			});
			
			//关于EDIT初始化
			$(".edit-tag").click(function() {
				var input = $(this).parents(".tag-item").find(".form-tag .input-tag");
				Tag.editClick($(input));
				$(input).focus();
				$(input).val($(input).val());
			});
			
			
			$(".input-tag").blur(function() {
				$(this).attr("readOnly", "readOnly");
			});
			
			//Tag值改变时修改数据
			$(".input-tag").change(function() {
				var val = $(this).val();
				if(val.trim() == "") {
					alert("请输入有效值");
					$(this).focus();
				} else {
					//修改数据
					var form = $(this).parents(".form-tag");
					Tag.editEvent($(form));
					$(this).blur();
				}
			});
			
			$(".btn-tag-show").click(function() {
				$(".input-tag-new").val("");
				Tag.display($(".tag-new"));
			});
			
			$(".btn-tag-add").click(function() {
				Tag.editEvent($(".form-tag-add"));
			});
			
			$(".btn-tag-cancel").click(function() {
				$(".input-tag-new").val("");
				Tag.hidden($(".tag-new"));
			});
			
			
			
		},
		display: function(object) {
			$(".tag-new").removeClass("hidden");
			$(".tag-new").addClass("display");
		},
		hidden: function(object) {
			$(".tag-new").removeClass("display");
			$(".tag-new").addClass("hidden");
		},
		editClick: function(input){
			$(input).attr("readonly", null);
		},
		editEvent: function(form) {
			$.ajax({
				url: $(form).attr("action") ,
				data: $(form).serialize(),
				type: "post",
				success: function(data) {
					//刷新页面
					location.reload();	
				},
				error: function(data) {
					alert(data);
				}
			});
		},
		deleteEvent: function(form) {
			$.ajax({
				url: "tag/delete" ,
				data: $(form).serialize(),
				type: "post",
				success: function(data) {
					//刷新页面
					location.reload();	
				},
				error: function(data) {
					location.reaload();
				}
			});
		}
		
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


var Word = {
		flag: false,
		init: function() {
			var input = $(".input-keyword");
			var btnDelete = $(".delete-word");
			var btnAdd = $(".add-word");
			
			//input事件绑定
			$(input).click(function() {
				$(this).attr("readonly", null);
			});
			
			$(input).blur(function() {
				$(this).attr("readonly", "readonly");	
			});
			
			$(input).change(function() {
				var val = $(this).val();
				if(val.trim() == "") {
					alert("请输入有效值");
					$(this).focus();
				} else {
					//修改数据
					var form = $(this).parents(".form-keyword");
					Word.editEvent($(form));
					$(this).blur();
				}
			});
			
			$(btnDelete).click(function() {
				Word.deleteEvent($(this).parents(".form-keyword"));
			});
			
			$(btnAdd).click(function() {
				if(Word.flag) return;
				var box = $(this).parents(".tag-item").find(".tag-words");
				var tagId = $(this).parents(".tag-item").find(".input-tag-id").val();
				console.log(box);
				var form = Word.prependWordForm(box, tagId);
				Word.init();
				$(form).find(".input-keyword").click();
				$(form).find(".input-keyword").focus();
				Word.flag = true;
			});
		},
		prependWordForm: function(box, tagId) {
			//外键关联
			var form = '<div class="col-md-12">' +　
			'<div class="row tag-word">' + 
			'<form class="form-keyword" action="tag/word" method="post" onsubmit="return false;">' +
			'<div class="col-md-8"><input class="input-keyword" name="keyword" readonly="readonly"/>' +
			'<input type="hidden" name="searchTag.tagId" value="' + tagId + '" />' +	
			'</div></form></div></div>'
			//添加word表单
			return $(box).prepend(form);
		},
		editEvent: function(form) {
			$.ajax({
				url: $(form).attr("action") ,
				data: $(form).serialize(),
				type: "post"
			});
		},
		deleteEvent: function(form) {
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
}

function addWordForm(element) {

	form = $(element).append(form);
	event();
	var input = $(form).find(".keyword").click();
	$(input).click();
	$(input).focus().select();
}