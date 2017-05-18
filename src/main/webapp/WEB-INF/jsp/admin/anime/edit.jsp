<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script type="text/javascript" src="<c:url value="/js/validate.js"/>"></script>

<form class="form-horizontal" action="anime/update"
	id="editAnimeForm" method="post" enctype="multipart/form-data">
	
	<div class="modal-content">
		<h4>编辑番剧信息</h4>
		<input name="id" type="hidden" value="${anime.id }"/>

		<div class="form-group">
			<label class="col-sm-3 control-label">名称</label>
			<div class="col-sm-6">
				<input class="form-control" name="name" placeholder="请输入番剧名称" 
					value="${anime.name }"/>
			</div>
		</div>

		<div class="form-group">
			<label class="col-sm-3 control-label">别名</label>
			<div class="col-sm-6">
				<input class="form-control" name="alias" placeholder="请输入番剧别名" 
					value="${anime.alias }"/>
			</div>
		</div>

		<div class="form-group">
			<div class="file-field input-field">
	    		<div class="btn col-sm-offset-1 col-sm-2">
	        		<span>上传图片</span>
	   		     	<input type="file" name="picFile">
	  	    	</div>
		   	   	<div class="file-path-wrapper col-sm-6">
		      		<input class="file-path validate" type="text" name="picFileText"
		      			value="${anime.pic }">
		      	</div>
	    	</div>
		</div>

		<div class="form-group">
			<label class="col-sm-3 control-label">描述</label>
			<div class="col-sm-6">
				<textarea class="form-control" name="description"
					placeholder="请输入描述" rows="4">${anime.description }</textarea>
			</div>
		</div>

		<div class="form-group">
			<label class="col-sm-3 control-label">类别</label>
			<div class="col-sm-6">
				<select class="form-control" name="type">
					<c:forEach var="animeType" items="${animeTypes.entrySet() }">
						<c:choose>
							<c:when test="${anime.type == animeType.key }">
								<option value="${animeType.key }" selected="selected">${animeType.value }</option>
							</c:when>
							<c:otherwise>
								<option value="${animeType.key }">${animeType.value }</option>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
			</div>
		</div>

		<div class="form-group">
			<label class="col-sm-3 control-label">开始连载日期</label>
			<div class="col-sm-6">
				<input class="form-control" name="publishMonth" type="date"
					value="<fmt:formatDate value='${anime.publishMonth }' pattern='yyyy-MM-dd'/>"/>
			</div>
		</div>

	</div>
	<div class="modal-footer">
		<input type="submit" class="btn btn-primary" value="保存" />
	</div>
</form>

