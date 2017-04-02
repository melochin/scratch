<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
 <%@ page isELIgnored="false" %>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
 <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> 
<script type="text/babel" src="<c:url value="/js/react-tag-video.js"/>" ></script>

<div class="row tag">
	<c:forEach var="tag" items="${tags }">
		<a id="${tag.tagId }">${tag.tagName }</a>	
	</c:forEach>
</div>

<div class="row" id="videos">

</div>