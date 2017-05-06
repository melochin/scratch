<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/babel" src="<c:url value="/js/react-common.js"/>"></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<div style="margin: 20px;">
	<div class="panel panel-default">
		<div class="panel-heading">
			<span class="panel-title"><b>用户管理</b></span> <span
				style="float: right">
				<button class="btn btn-success btn-sm"
					data-toggle="modal" data-target="#modal-save">创建新用户</button>
			</span>
		</div>
		<div class="panel-body">
			<table class="table table-hover">
				<thead>
					<tr>
						<th>用户名</th>
						<th>邮箱</th>
						<th>状态</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="user" items="${userList.data }">
						<tr>
							<td>${user.username }</td>
							<td>${user.email }</td>
							<td><c:choose>
									<c:when test="${user.status == '1' }">
										激活
									</c:when>
									<c:otherwise>
										未激活
									</c:otherwise>
								</c:choose></td>
							<td>
								<button class="btn btn-link btn-sm" data-toggle="modal"
									data-target="#modal-edit" onclick="getUserInfo(${user.userId})">编辑</button>
								<a class="btn btn-link btn-sm" href="delete?userId=${user.userId }">删除</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<nav aria-label="Page navigation">
				<ul class="pagination">
					<c:if test="${userList.page.curPage - 1  >= 1}">
						<li><a href="index?p=${userList.page.curPage - 1 }"
							aria-label="Previous"> <span aria-hidden="true">&laquo;</span>
						</a></li>
					</c:if>
					<li><a href="index?p=${userList.page.curPage } ">${userList.page.curPage }</a></li>
					<c:if
						test="${userList.page.curPage + 1 <= userList.page.totalPage }">
						<li><a href="index?p=${userList.page.curPage + 1 }"
							aria-label="Next"> <span aria-hidden="true">&raquo;</span>
						</a></li>
					</c:if>
				</ul>
			</nav>
		</div>
	</div>
</div>

<script type="text/javascript">
$("[name='status']").bootstrapSwitch();

var $form = $("form[name='user-info']");

function saveUser() {
	var $status = $form.find("input[name='status']");
	if($status.val() == 'on') {
		$status.val('1');
	} else {
		$status.val('0');
	}
	
	$form.submit();
}

function getUserInfo(userId) {
	var $form = $("form[name='user-info']");
	var $username = $form.find("input[name='username']");
	var $userId = $form.find("input[name='userId']");
	var $status = $form.find("input[name='status']");
	var $email = $form.find("input[name='email']");
	$status.bootstrapSwitch();
	$.ajax({
		url:userId,
		type: "get"
	})
	.done(function(result) {
		if(result.error != null) {
			alert(result.error);
		} else {
			console.debug(result.data);
			console.debug($form.serialize());
			$username.val(result.data.username);
			$userId.val(result.data.userId);
			$email.val(result.data.email);
			if(result.data.status == '0') {
				$status.bootstrapSwitch('state', false);
			} else {
				$status.bootstrapSwitch('state', true);
			}
		}
	})
}
</script>
