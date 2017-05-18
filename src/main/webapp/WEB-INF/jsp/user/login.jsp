<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<script type="text/javascript" src="<c:url value="/js/validate.js"/>"></script>

<div class="row">
	<div class="col-md-offset-4 col-md-4 col-xs-offset-3 col-xs-6 
		login-form" role="login">
			<form action="login" method="post"  id="userLoginForm">
				<div class="form-group">
					<input class="form-control" type="text" name="username" placeholder="用户名" />
				</div>
				<div class="form-group">
					<input class="form-control" type="password" name="password" placeholder="密码" />
				</div>
				<span class="error">${error }</span>
				<div>
					<input type="checkbox" /> 
					<label>记住我</label>
				</div>
				<div class="row">
					<input class="btn btn-primary col-md-4" type="submit" value="登录"/>
					<a class="btn btn-default col-md-4" href="<c:url value="/user/register"/>" >注册</a>
				</div>
			</form>
			<div class="row">
				<div class="col-md-6">
					<a href="<c:url value="/user/reset" />"><h6>忘记密码？</h6></a>
				</div>
				<div class="col-md-6">
					<a><h6>激活账号</h6></a>
				</div>
			</div>
	</div>
</div>
