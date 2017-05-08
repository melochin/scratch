<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- Modal -->
<script type="text/javascript" src="<c:url value="/js/validate.js"/>"></script>


<form class="form-horizontal" name="user-info" action="save" id="userRegisterForm"
	method="post">
	<div class="modal-body">
		<div class="form-group">
			<label class="col-sm-3 control-label">用户名</label>
			<div class="col-sm-6">
				<input class="form-control" name="username" placeholder="请输入用户名" />
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-3 control-label">邮箱</label>
			<div class="col-sm-6">
				<input class="form-control" name="email" placeholder="请输入邮箱" />
			</div>
		</div>

		<div class="form-group">
			<label class="col-sm-3 control-label">密码</label>
			<div class="col-sm-6">
				<input class="form-control" name="password" type="password"
					placeholder="请输入密码"/>
			</div>
		</div>

		<div class="form-group">
			<label class="col-sm-3 control-label">激活状态</label>
			<div class="col-sm-6 switch" id="toggle-state-switch">
				<input class="form-control" name="status" type="checkbox" value="1" />
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
		<input type="submit"  class="btn btn-primary" value="保存"/>
	</div>
</form>

<script>
	$("[name='status']").bootstrapSwitch();
</script>