<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="t"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript"
	src="<c:url value="/js/tools/jquery.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/bootstrap/js/bootstrap.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/bootstrap/js/bootstrap-switch.js"/>"></script>
<script src="<c:url value="/js/tools/browser.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/tools/react.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/js/tools/react-dom.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/formvalidation/js/formValidation.min.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/formvalidation/js/bootstrap.min.js"/>"></script>
<!-- 暂不处理 -->
<script type="text/javascript" src="<c:url value="/js/myJquery.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/tag.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/common.js"/>"></script>
<!-- css -->
<link href="<c:url value="/bootstrap/css/bootstrap.css"/>"
	rel="stylesheet"></link>
<link href="<c:url value="/bootstrap/css/bootstrap-switch.css"/>"
	rel="stylesheet"></link>
<link
	href="<c:url value="/formvalidation/css/formValidation.min.css" />"
	rel="stylesheet"></link>
<link href="<c:url value="/css/common.css"/>" rel="stylesheet"></link>
<link href="<c:url value="/css/mycss.css"/>" rel="stylesheet"></link>
<title><t:getAsString name="title" ignore="true" /></title>
</head>
<body>
	<div class="row">
		<t:insertAttribute name="header" />
	</div>
	<div class="row" style="padding: 35px 0 0 0;">
		<div class="col-md-1">
			<t:insertAttribute name="nav" />
		</div>
		<div class="col-md-10 content-box">
			<div class="container">
				<t:insertAttribute name="content" />
			</div>
		</div>
	</div>
</body>
</html>
