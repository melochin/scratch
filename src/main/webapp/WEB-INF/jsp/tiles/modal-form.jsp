<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t"%>

<div style="margin: 20px;">
	<div class="panel panel-default">
		<div class="panel-heading">
			<span class="panel-title">
				<b>
					<t:insertAttribute name="title" ignore="true"/>管理
				</b>
			</span> 
			<span style="float: right">
				<button class="btn btn-success btn-sm"
					data-toggle="modal" data-target="#modal-save">
					新增<t:insertAttribute name="title" ignore="true"/>	
				</button>
			</span>
		</div>
		<div class="panel-body">
			<t:insertAttribute name="form" />
		</div>
	</div>
</div>    

<div class="modal fade" id="modal-save" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="myModalLabel">
					新增<t:insertAttribute name="title" ignore="true"/>	
				</h4>
			</div>
			<t:insertAttribute name="modal-save" />
		</div>
	</div>
</div>

<t:insertAttribute name="modal-edit" ignore="true"/>

<div class="modal fade" id="modal-edit" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
		</div>
	</div>
</div>

<script type="text/javascript">
<!-- 移除模态框的数据，为了触发重复加载页面 -->
$("#modal-edit").on("hidden.bs.modal", function() {
    $(this).removeData("bs.modal");
});
</script>