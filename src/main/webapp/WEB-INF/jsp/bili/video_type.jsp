<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>

<div class="col-md-4">
	<ul class="list-group">
		<c:forEach var="type" items="${videoTypes }">
			<li class="list-group-item parent-type">
				<div class="row">
					<div class="col-md-1">
						<b class="parent-code">${type.code }</b>
					</div>
					<div class="col-md-8">
						<b>${type.name }</b>
					</div>
					<div class="col-md-2">
						<div class="btn-group">
							<button class="btn btn-default btn-sm dropdown-toggle"
								type="button" data-toggle="dropdown" aria-haspopup="true"
								aria-expanded="false">
								<span class="glyphicon glyphicon-cog"></span>
							</button>
							<ul class="dropdown-menu">
								<li><a>编辑</a> <a class="insert" data-toggle="modal" data-target="#myModal">新增</a> <a>删除</a></li>
							</ul>
						</div>
					</div>
				</div>
				<ul class="list-group">
					<c:forEach var="child" items="${type.childTypes }">
						<li class="list-group-item child">
							<div class="row child-type">
								<form>
									<div class="col-md-1">
										<input name="code" value="${child.code }" type="text"
											readonly="readonly" /> <input name="parentType.code"
											value="${type.code }" type="hidden" />
									</div>
									<div class="col-md-5">
										<input name="name" value="${child.name }" type="text"
											readonly="readonly" />
									</div>
								</form>
							</div> <!-- 具体操作 -->
							<div class="row child-edit" style="display: none;">
								<div class="col-md-1"></div>
								<div class="col-md-6">
									<button type="button" class="btn btn-sm btn-success save">保存</button>
									<button type="button" class="btn btn-sm btn-default cancel">取消</button>
								</div>
								<div class="col-md-2">
									<button type="button" class="btn btn-sm btn-link delete">删除</button>
								</div>
							</div>
						</li>
					</c:forEach>
				</ul>
			</li>
		</c:forEach>
	</ul>
</div>

<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="myModalLabel">新增视频类别</h4>
      </div>
        
      <div class="modal-body">
      	<div class="row">
      		<div class="col-md-2"></div>
	      	<div class="col-md-8">
		      	<form class="form-horizontal">
		      		<div class="form-group">
		      			<label>编号</label>
			      		<input class="form-control" placeholder="数字" name="code" type="text"/>
		      		</div>
		      		<div class="form-group">
		      			<label>类别名称</label>
			      		<input class="form-control" name="name" type="text"/>
		      		</div>
		      		<div class="form-group">
		      			<label>所属类别</label>
		      			<select class="form-control" name="parentType.code">
							<c:forEach var="type" items="${videoTypes }">
			      				<option value="${type.code }">${type.name }</option>
							</c:forEach>		      				
		      			</select>
		      		</div>
		      		<input name="parentType.code" type="hidden"/>
		      	</form>
	      	</div>
      	</div>
      </div>
      
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button type="button" class="btn btn-primary save">Save changes</button>
      </div>
    </div>
    
  </div>
</div>


<script type="text/javascript">
$(document).ready(function(){
	
	$(".child-type").click(function() {
		var $edit =	$(this).siblings(".child-edit");
		var $form = $(this).find("form");
		var $input = $form.find("input[name='name']");
		//显示编辑按钮
		$edit.css("display", "block");
		//输入框更改为可编辑状态
		$input.attr("readonly", null);
		$input.select();
	});
	
	
	$("#myModal").each(function() {
		var $form = $(this).find("form");
		//保存
		$(this).find(".save").click(function() {
			$.ajax({
				url: "type/insert",
				data: $form.serialize(),
				type: "POST"
			})
			.done(function(result) {
				if(result.error != null && result.error != "") {
					alert(result.error);
				} else {
					//保存成功刷新页面
					window.location.reload(); 
				}
			})
			.fail(function() {
				alert("服务器出错");
			})
		});
	});
	
	$(".parent-type").each(function() {
		var $this = $(this);
		var $insert = $this.find(".insert");
		var $modal = $(".modal-body");
		//定义新增事件
		$insert.click(function() {
			var code = $this.find(".parent-code").text()
			//清空表单的输入情况			
			$modal.find("input").val("");
			$modal.find("select").find("option[value='" + code + "']").attr("selected", true);
		});
				
	});
	
	
	$(".child-type").each(function() {
		var $edit =	$(this).siblings(".child-edit");
		var $form = $(this).find("form");
		var $input = $form.find("input[name='name']");
		var saving = false;
		var origin = $input.val();
		
		//定义取消事件
		$edit.find(".cancel").click(function() {
			$edit.css("display", "none");
			//文本恢复
			$input.val(origin);
			$input.attr("readonly", "readonly");
		});
		
		//定义保存事件
		$edit.find(".save").click(function() {
			if(saving) return;
			saving = true;
			$.ajax({
				url: "type/update",
				data: $form.serialize(),
				type: "POST"
			})
			.done(function() {
				$edit.css("display", "none");
				$input.attr("readonly", "readonly");				
			})
			.fail(function() {
				alert("服务器出错");
			})
			.always(function() {
				saving = false;
			})
		});
		
		//定义删除事件
		$edit.find(".delete").click(function() {
			$.ajax({
				url: "type/delete",
				data: $form.serialize(),
				type: "POST"
			})
			.done(function(result){
				if(result.error != null && result.error != "") {
					alert(result.error);					
				} else {
					$edit.parent().remove();
				}
			})
		});
	});
	
});
</script>

