<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<!-- 热门 -->
<div class="row video-section">
	<div class="row">
		<div class="col-md-5">
			<h3>热门</h3>
		</div>
	</div>
	<div class="row" id="hot">
		<div class="videos">
			<div class="spinner">
				<div class="double-bounce1"></div>
		 		<div class="double-bounce2"></div>
			</div>
		</div>
	</div>
</div>
<!-- 关注 -->
<div class="row video-section">
	<div class="row">
		<div class="col-md-5">
			<h3><a>关注</a></h3>
		</div>
	</div>
	<div class="row" id="follow">
		<div class="videos">
			<div class="spinner">
				<div class="double-bounce1"></div>
		 		<div class="double-bounce2"></div>
			</div>
		</div>
	</div>
</div>
<!-- 视频分类 -->
<c:forEach var="type" items="${types }">
	<div class="row video-section">
		<div class="row">
			<div class="col-md-5">
				<h3><a>${type.name }</a></h3>
			</div>
		</div>
		<div class="row" id="follow">
			<div class="videos videos-type" id="${type.code }">
				<div class="spinner">
					<div class="double-bounce1"></div>
			 		<div class="double-bounce2"></div>
				</div>
			</div>
		</div>
	</div>
</c:forEach>
