<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html; UTF-8">

<div class="col-md-offset-4 col-md-4 form">
	<div class="panel panel-primary">
		<div class="panel-heading">
			<label>重置密码</label>
		</div>
		<div class="panel-body">
			<form class="form-valid" action="resetmail" method="post">
				<div class="form-group">
					<input class="form-control" name="username" placeholder="用户名" />
				</div>
				<div class="form-group">
					<input class="form-control" name="email" placeholder="邮箱"
						jerror="邮箱格式不正确" 
						jpattern="^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$"
					 />
				</div>
				<div class="form-group">
					<input class="btn btn-primary"type="submit" />
				</div>
			</form>
		</div>
	</div>
</div>
