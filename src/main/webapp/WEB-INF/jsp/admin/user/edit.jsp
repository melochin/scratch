<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!-- Modal -->
<div class="modal fade" id="modal-edit" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="myModalLabel">编辑用户信息</h4>
			</div>
			<div class="modal-body">
				<form class="form-horizontal" name="user-info" action="update"
					method="post">
					<input name="userId" type="hidden" />
					<div class="form-group">
						<label class="col-sm-3 control-label">用户名</label>
						<div class="col-sm-6">
							<input class="form-control" name="username" />
						</div>
					</div>
					<div class="form-group">
						<label class="col-sm-3 control-label">邮箱</label>
						<div class="col-sm-6">
							<input class="form-control" name="email" />
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