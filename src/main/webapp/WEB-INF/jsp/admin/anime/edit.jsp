<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    
<div class="modal fade" id="modal-edit" tabindex="-1" role="dialog"
	aria-labelledby="myModalLabel">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="myModalLabel">编辑番剧信息</h4>
			</div>
			<div class="modal-body">
				<form class="form-horizontal" action="anime/update"
					method="post">
					
					<input name="id" type="hidden" />
					
					<div class="form-group">
						<label class="col-sm-3 control-label">名称</label>
						<div class="col-sm-6">
							<input class="form-control" name="name" placeholder="请输入番剧名称" />
						</div>
					</div>
					
					<div class="form-group">
						<label class="col-sm-3 control-label">别名</label>
						<div class="col-sm-6">
							<input class="form-control" name="alias" placeholder="请输入番剧别名" />
						</div>
					</div>

					<div class="form-group">
						<label class="col-sm-3 control-label">图片链接</label>
						<div class="col-sm-6">
							<input class="form-control" name="pic" placeholder="请输入图片链接" />
						</div>
					</div>

					<div class="form-group">
						<label class="col-sm-3 control-label">描述</label>
						<div class="col-sm-6">
							<textarea class="form-control" name="description" placeholder="请输入描述" rows="4">
							</textarea>
						</div>
					</div>
					
					<div class="form-group">
						<label class="col-sm-3 control-label">开始连载日期</label>
						<div class="col-sm-6">
							<input class="form-control" name="publishMonth" type="date" />
						</div>
					</div>
					
				</form>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
				<button type="button" class="btn btn-primary" onclick="updateAnime()">保存</button>
			</div>
		</div>
	</div>
</div>
