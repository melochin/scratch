<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
 <%@ page isELIgnored="false" %>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
 <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> 

<div class="row tool">
	<div class="col-md-3">
		<button class="btn btn-default btn-tag-show">+ 新增标签</button>
		<div class="tag-new hidden row">
			<form class="form-tag-add" action="tag" method="post">
				<div class="row">
					<input class="input-tag-new col-md-8" name="tagName" placeholder="标签名"/>
					<select class="input-tag-new col-md-8" placeholder="类型" name="type.id">
						<c:forEach var="type" items="${types }">
						 	<option value="${type.id }">${type.name }</option>
						</c:forEach>
					</select>				
				</div>
				<div class="row">
					<input class="btn btn-success btn-sm btn-tag-add" type="button" value="提交"/>
					<input class="btn btn-default btn-sm btn-tag-cancel" type="button" value="取消"/>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="row">
	<c:forEach var="tag" items="${searchTags }" varStatus="i" >
		<c:if test="${(i.index % 10) == 0 }" >
		<div class="col-md-3">
		<ul class="list-group">
		</c:if>
				<li class="list-group-item tag-item">
					<div class="row">
						<div class="col-md-6 tag-name">
							<form class="form-tag" action="tag">
								<input class="input-tag col-md-12" name="tagName" 
									value="${tag.tagName }" readonly="readonly"/>
								<input class="input-tag-id" name="tagId" value="${tag.tagId }" type="hidden"/>
							</form>
						</div>
						<div class="col-md-4">
							<span style="font-size:10px">${tag.type.name }</span>
							<b class="badge">${fn:length(tag.searchKeyWords) }</b>
						</div>
						<div class="col-md-1">
							<span class="glyphicon glyphicon-option-horizontal dropdown-toggle" data-toggle="dropdown"></span>
							<ul class="dropdown-menu">
							  <li>
							  	<a class="edit-tag" href="#"><span class="glyphicon glyphicon-pencil"></span>编辑标签</a>
							  </li>
							  <li>
							  	<a class="delete-tag" href="#">
							  		<span class="glyphicon glyphicon-trash"></span>
							  		删除标签
							  	</a>
							  	<a class="add-word" href="#">
							  		<span class="glyphicon glyphicon-plus"></span>
							  		新增关键字
							  	</a>
							  </li>
							</ul>
						</div>
					</div>
					<div class="row tag-words">
						<c:if test="${fn:length(tag.searchKeyWords) > 0 }">
								<c:forEach var="words" items="${tag.searchKeyWords }" varStatus="l"> 
									<div class="col-md-12">
										<div class="row tag-word">
											<form class="form-keyword" action="tag/word" method="post" onsubmit="return false;">
												<div class="col-md-8">
													<input class="input-keyword" name="keyword" value="${words.keyword }" readonly="readonly"/>
													<input type="hidden" name="searchId" value="${words.searchId }" />
													<input type="hidden" name="searchTag.tagId" value="${tag.tagId }" />
												</div>
												<div class="col-md-4 edit">
													<a class="delete-word" href="#"><span class="glyphicon glyphicon-trash"></span></a>
												</div>
											</form>
										</div>
									</div>
								</c:forEach>
						</c:if>
					</div>
				</li>
		<c:if test="${(i.index + 1) % 10 == 0 || i.index == fn:length(searchTags) }" >
		</ul>
		</div>
		</c:if>
	</c:forEach>
</div>
	
