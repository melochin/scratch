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

