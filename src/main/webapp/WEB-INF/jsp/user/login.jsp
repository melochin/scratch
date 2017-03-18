<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<div class="row">
	<div class="col-md-4"></div>
	
	<div class="col-md-4 login-form">
		<section class="login-form">
			<form action="login" method="post" role="login" class="form-valid" >
				<input class="form-control" type="text" name="username" placeholder="用户名" 
					jerror="用户名不能为空" jpattern="^.{3,30}$"/>
				<input class="form-control" type="password" name="password" placeholder="密码" 
					jerror="密码不能为空" jpattern="^.{6,30}$">
				<span class="error">${error }</span>
				<div class="checkbox">
					<label>
						<input type="checkbox" /> 记住我
					</label>
				</div>
				<div class="row">
					<input class="btn btn-primary col-md-4" type="submit" value="登录"/>
					<a class="btn btn-default col-md-4" href="<c:url value="/user/register"/>" >注册</a>
				</div>
			</form>
		</section>
	</div>
	
	<div class="col-md-4" ></div>
</div>
