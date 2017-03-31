<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<div class="row">
	<div class="col-md-offset-4 col-md-4">
		<div class="panel panel-primary">
			<div class="panel-heading">
				<label>重置密码</label>
			</div>
			<div class="panel-body">
				<form class="form-horizontal form-valid" action="resetpwd" method="post">
					<div class="form-group">
						<label class="col-md-3 control-label">新密码</label>
						<div class="col-md-9">
							<input class="form-control" name="password" placeholder="输入新密码" type="password"
								jerror="密码只能以数字或字母组合，且长度不能少于6位超过18位" 
								jpattern="^[a-zA-Z0-9_]{6,18}$"/>
						</div>
					</div>
					<div class="form-group">
						<label class="col-md-3 control-label">确认密码</label>
						<div class="col-md-9">
							<input class="col-md-10 form-control" name="repassword" placeholder="确认新密码" type="password"
								jerror="与上次输入的密码不同" 
								jpattern="^[a-zA-Z0-9_]{6,18}$" jsameas="password" />
						</div>
					</div>
					<div class="form-group">
						<div class="col-md-offset-3 col-md-9">
							<input class="btn btn-primary" type="submit">
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>