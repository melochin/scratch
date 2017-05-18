<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/babel" src="<c:url value="/js/react-common.js"/>"></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

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
				<td><a class="btn btn-link btn-sm" data-toggle="modal"
					data-target="#modal-edit" href="form/${user.userId }">编辑</a> <a
					class="btn btn-link btn-sm" href="delete?userId=${user.userId }">删除</a>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<div id="page" data-page="${userList.page.curPage }" data-total="${userList.page.totalPage }" 
	data-url="user?p=" >
</div>
