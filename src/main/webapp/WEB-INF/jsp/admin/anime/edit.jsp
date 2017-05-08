<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt"%>

<div class="modal-header">
	<button type="button" class="close" data-dismiss="modal"
		aria-label="Close">
		<span aria-hidden="true">&times;</span>
	</button>
	<h4 class="modal-title" id="myModalLabel">编辑番剧信息</h4>
</div>

<form class="form-horizontal" action="anime/update"
	id="editAnimeForm" method="post">
	<div class="modal-body">

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
			<label class="col-sm-3 control-label">图片链接</label>
			<div class="col-sm-6">
				<input class="form-control" name="pic" placeholder="请输入图片链接" 
					value="${anime.pic }"/>
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
			<label class="col-sm-3 control-label">开始连载日期</label>
			<div class="col-sm-6">
				<input class="form-control" name="publishMonth" type="date"
					value="<fmt:formatDate value='${anime.publishMonth }' pattern='yyyy-MM-dd'/>"/>
			</div>
		</div>

	</div>
	<div class="modal-footer">
		<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
		<input type="submit" class="btn btn-primary" value="保存" />
	</div>
</form>

