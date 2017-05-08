<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<script type="text/javascript" src="<c:url value="/js/validate.js"/>"></script>

<body>
	<div class="row">
		<div class="col-md-4"></div>
		<div class="col-md-4">
			<section class="login-form form-valid">
				<form action="register" method="post" id="userRegisterForm" role="login">
					<div class="form-group">
						<input id ="username" class="form-control" name="username" value="${user.username }"
							placeholder="账号" />
					</div>
					<div class="form-group">
						<input class="form-control" name="email" value="${user.email }" 
							placeholder="邮箱" />
					</div>
					<div class="form-group">
						<input class="form-control" name="password" type="password" 
							placeholder="密码" />
					</div>
					<div class="form-group">
						<input class="form-control" name="rePassword" type="password" 
							placeholder="确认密码" />
					</div>
					<div class="row">
						<input class="btn btn-primary col-md-11" type="submit" value="注册" />
					</div>
				</form>		
			</section>
		</div>
		<div class="col-md-4"></div>
	</div>
</body>