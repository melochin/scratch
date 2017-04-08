<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
 <%@ page isELIgnored="false" %>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
 <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> 
<script type="text/babel" src="<c:url value="/js/react-tag-video.js"/>" ></script>

<div class="row">
	<a href="search/tag"><button class="btn btn-default">设置关注标签</button></a>
</div>

<div class="row tag" style="padding:10px 0;">
	<c:if test="${fn:length(tags) > 0}">
		<a id="0">全部</a>
	</c:if>
	<c:forEach var="tag" items="${tags }">
		<a id="${tag.tagId }">${tag.tagName }</a>	
	</c:forEach>
</div>

<div class="row" id="videos-follow">

</div>