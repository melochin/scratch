
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>

<table class="table table-hover">
				<colgroup>
	<col style="width:10%">
	<col style="width:30%">
	<col style="width:10%">
	<col style="width:10%">
	<col style="width:10%">
	<col style="width:40%">
</colgroup>
<thead>
	<tr>
		<th>名称</th>
		<th>描述</th>
		<th>开始连载月份</th>
		<th>是否完结</th>
		<th>类型</th>
		<th>操作</th>
	</tr>
</thead>
<tbody>
	<c:forEach var="anime" items="${animeList }">
		<tr>
			<td>${anime.name}</td>
			<td>
				<div style="overflow: hidden; max-height:50px;">
					<span>${anime.description }</span>
				</div>
			</td>
			<td>
				<fmt:formatDate value="${anime.publishMonth }" type="date"/>
			</td>
			<td>
				<c:choose>
					<c:when test="${anime.finished == false }">
						否
					</c:when>
					<c:otherwise>
						是
					</c:otherwise>
				</c:choose>
			</td>
			<td>
				${animeTypes[anime.type]}
			</td>
			<td>
  				<button data-target="modal-edit" data-href="anime/form/${anime.id }" class="btn">编辑</button>
				<button data-target="modal-edit" data-href="anime/link/${anime.id }" class="btn">关联</button>			
				<a class="btn btn-link btn-sm" href="anime/delete/${anime.id }">删除</a>
			</td>
		</tr>
	</c:forEach>
	</tbody>
</table>

<div id="modal-edit" class="modal modal-fixed-footer"></div>
