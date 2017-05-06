<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>

<div style="margin: 20px;">
	<div class="panel panel-default">
		<div class="panel-heading">
			<span class="panel-title"><b>番剧管理</b></span> <span
				style="float: right">
				<button class="btn btn-success btn-sm" onClick="insertUser()"
					data-toggle="modal" data-target="#modal-save">创建番剧</button>
			</span>
		</div>
		<div class="panel-body">
			<table class="table table-hover">
				<colgroup>
					<col style="width:20%">
					<col style="width:40%">
					<col style="width:10%">
					<col style="width:10%">
					<col style="width:10%">
				</colgroup>
				<thead>
					<tr>
						<th>名称</th>
						<th>描述</th>
						<th>开始连载月份</th>
						<th>是否完结</th>
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
								<button class="btn btn-link btn-sm" data-toggle="modal"
									data-target="#modal-edit" onclick="getAnimeInfo(${anime.id})">编辑</button>
								<a class="btn btn-link btn-sm" href="anime/delete/${anime.id }">删除</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>    


<script type="text/javascript">
function getAnimeInfo(animeId) {
	var $form = $("#modal-edit").find("form");
	
	var $id = $form.find("input[name='id']");
	var $name = $form.find("input[name='name']");
	var $alias = $form.find("input[name='alias']");
	var $pic = $form.find("input[name='pic']");
	var $description = $form.find("textarea[name='description']");
	var $publishMonth = $form.find("input[name='publishMonth']");
	
	$.ajax({
		url:"anime/" + animeId,
		type: "get"
	})
	.done(function(result) {
		console.debug(result);
		console.debug($name);
		if(result.error != null) {
			alert(result.error);
		} else {
			$id.val(result.data.id);
			$name.val(result.data.name);
			$alias.val(result.data.alias);
			$pic.val(result.data.pic);
			$description.val(result.data.description);
			var date = new Date(result.data.publishMonth).format("yyyy-MM-dd") 
			$publishMonth.val(date);
		}
	})
}

function updateAnime() {
	var $form = $("#modal-edit").find("form");
	$form.submit();
}


</script>
