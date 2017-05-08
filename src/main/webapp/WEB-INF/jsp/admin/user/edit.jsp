<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<script type="text/javascript" src="<c:url value="/js/validate.js"/>"></script>

<!-- Modal -->
<form class="form-horizontal" name="user-info" action="update" id="userEditForm"
	method="post">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-label="Close">
			<span aria-hidden="true">&times;</span>
		</button>
		<h4 class="modal-title" id="myModalLabel">编辑用户信息</h4>
	</div>
	<div class="modal-body">
		<input name="userId" type="hidden" value="${user.userId }"/>
		<div class="form-group">
			<label class="col-sm-3 control-label">用户名</label>
			<div class="col-sm-6">
				<input class="form-control" name="username" value="${user.username }"/>
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-3 control-label">邮箱</label>
			<div class="col-sm-6">
				<input class="form-control" name="email" value="${user.email }"/>
			</div>
		</div>

		<div class="form-group">
			<label class="col-sm-3 control-label">密码</label>
			<div class="col-sm-6">
				<input class="form-control" name="password" type="password" />
			</div>
		</div>

		<div class="form-group">
			<label class="col-sm-3 control-label">激活状态</label>
			<div class="col-sm-6 switch" id="toggle-state-switch">
				<c:choose>
					<c:when test="${user.status == '1' }">
						<input class="form-control" name="status" type="checkbox" value="1" checked="checked"/>
					</c:when>
					<c:otherwise>
						<input class="form-control" name="status" type="checkbox" value="1"/>					
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
		<input type="submit" class="btn btn-primary" value="保存" />
	</div>
</form>

<script type="text/javascript">
$(document).ready(function() {
	$("[name='status']").bootstrapSwitch();
	
})
</script>
