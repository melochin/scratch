<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    
<div class="row" id="nav-button">
    <div class="btn-group-vertical" role="group" aria-label="Default button group">
        <a href="<c:url value='/'/>">
        	<button type="button" class="btn btn-sm">首页</button>
        </a>
        <a href="<c:url value='/follow'/>" >
        	<button type="button" class="btn btn-sm">关注</button>
        </a>
        <a href="<c:url value=''/>">
	        <button type="button" class="btn btn-sm">影视</button>
        </a>
        <a href="<c:url value=''/>">
	        <button type="button" class="btn btn-sm">娱乐</button>
        </a>
	</div>
</div>