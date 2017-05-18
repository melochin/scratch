<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t"%>

<div style="margin: 20px;">
	<div class="panel panel-default">
		<div class="panel-heading">
			<div class="row">
				<div class="col-md-6">
					<span class="panel-title"> <b> <t:insertAttribute
								name="title" ignore="true" />管理
					</b>
					</span>
				</div>
				<div class="col-md-offset-5 col-md-1">
					<span>
						<button class="btn btn-success btn-sm" data-toggle="modal"
							data-target="#modal-save">
							新增<t:insertAttribute name="title" ignore="true" />
						</button>
					</span>
				</div>
			</div>
		</div>
		<div class="panel-body">
			<t:insertAttribute name="form" />
		</div>
	</div>
</div>


 <!-- Modal Structure -->
 <div id="modal-save" class="modal modal-fixed-footer">
 	<t:insertAttribute name="modal-save" />
 </div>

<t:insertAttribute name="modal-edit" ignore="true" />

<div class="modal modal-fixed-footer" id="modal-edit" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel">
</div>

<script type="text/javascript">
<!-- 移除模态框的数据，为了触发重复加载页面 -->
	$("#modal-edit").on("hidden.bs.modal", function() {
		$(this).removeData("bs.modal");
	});
</script>