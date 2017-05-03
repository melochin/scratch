<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!-- Modal -->
<div class="modal fade" id="modal-save" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="myModalLabel">新增用户</h4>
			</div>
			<div class="modal-body">
				<form class="form-horizontal" name="user-info" action="save"
					method="post">
					<div class="form-group">
						<label class="col-sm-3 control-label">用户名</label>
						<div class="col-sm-6">
							<input class="form-control" name="username" placeholder="请输入用户名"
								jerror="用户名只能以数字或字母组合，且长度不能少于6位超过18位" jpattern="^[a-zA-Z0-9_]{6,18}$"/>
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">邮箱</label>
						<div class="col-sm-6">
							<input class="form-control" name="email" placeholder="请输入邮箱" 
								jerror="邮箱格式不正确" jpattern="^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$"/>
						</div>
					</div>

					<div class="form-group">
						<label class="col-sm-3 control-label">密码</label>
						<div class="col-sm-6">
							<input class="form-control" name="password" type="password"  placeholder="请输入密码" 
								jerror="密码只能以数字或字母组合，且长度不能少于6位超过18位" jpattern="^[a-zA-Z0-9_]{6,18}$"/>
						</div>
					</div>

					<div class="form-group">
						<label class="col-sm-3 control-label">激活状态</label>
						<div class="col-sm-6 switch" id="toggle-state-switch">
							<input class="form-control" name="status" type="checkbox" value="1"/>
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				<button type="button" class="btn btn-primary" onclick="saveUser()">保存</button>
			</div>
		</div>
	</div>
</div>

<script>
	$("[name='status']").bootstrapSwitch();
	function saveUser() {
		var $form = $("form");
		$form.submit();
	}
</script>