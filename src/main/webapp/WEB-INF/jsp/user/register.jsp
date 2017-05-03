<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<body>
	<div class="row">
		<div class="col-md-4"></div>
		<div class="col-md-4">
			<section class="login-form form-valid">
				<form action="register" method="post" role="login">
					<input id ="username" class="form-control" name="username" value="${user.username }"
						placeholder="账号" jerror="用户名只能以数字或字母组合，且长度不能少于6位超过18位" jpattern="^[a-zA-Z0-9_]{6,18}$"/>
					<input class="form-control" name="email" value="${user.email }"
						placeholder="邮箱" jerror="邮箱格式不正确" jpattern="^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$" />
					<input class="form-control" name="password" type="password" 
						placeholder="密码" jerror="密码只能以数字或字母组合，且长度不能少于6位超过18位" jpattern="^[a-zA-Z0-9_]{6,18}$" />
					<input class="form-control" name="rePassword" type="password" 
						placeholder="确认密码" jerror="与上次输入的密码不同" jsameas="password" />
					<span id="error" class="error">${error}</span>
					<div class="row">
						<input class="btn btn-primary col-md-11" type="submit" value="注册" />
					</div>
				</form>		
			</section>
		</div>
		<div class="col-md-4"></div>
	</div>
</body>