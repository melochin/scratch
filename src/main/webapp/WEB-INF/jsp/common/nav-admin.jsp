<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>

<ul class="collapsible collection" data-collapsible="accordion">
	<li>
		<a class="collection-item">首页</a>
	</li>
	<li>
		<div class="collapsible-header collection-item">后台管理</div>
		<div class="collapsible-body collection" style="padding:0px;">
	        <a href="<c:url value='/admin/user' />" class="collection-item">用户管理</a>
	        <a href="<c:url value='/admin/anime' />" class="collection-item">番剧管理</a>
		</div>
	</li>
</ul>

<script>

$(document).ready(function() {
	$(".sidebar-caret a").click(function() {
		$(".wrapper").toggleClass("toggled");
	});	
	var li = findLi();
	if(li != null) {
		$(".navi li").removeClass("active");
		$(li).addClass("active");
	}
});

function findLi() {
	//get current url
	var curUrl = window.location.pathname;
	//get href of a
	var list = $(".sidebar-nav a");
	for(var i=0; i<list.length; i++) {
		var a = $(list.get(i));
		if(a.attr("href") == curUrl) {
			return a.parent("li");
		}
	}
	return null;
}
</script>