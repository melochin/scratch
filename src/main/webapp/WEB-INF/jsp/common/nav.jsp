<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<div>
	<div class="row">
		<div class="col-xs-2 col-xs-offset-8 sidebar-caret" >
			<a href onclick="return false;"><span><<</span></a>
		</div>
	</div>
    <ul class="sidebar-nav">
        <li><a href="<c:url value="/"/>"><i class="glyphicon glyphicon-home" aria-hidden="true"></i><span class="hidden-xs hidden-sm">Home</span></a></li>
        <li><a href='<c:url value="/search/tag"/>'><i class="glyphicon glyphicon-tags" aria-hidden="true"></i><span class="hidden-xs hidden-sm">Tag</span></a></li>
        <li><a href="#"><i class="glyphicon glyphicon-tags" aria-hidden="true"></i><span class="hidden-xs hidden-sm">Statistics</span></a></li>
        <li><a href="#"><i class="glyphicon glyphicon-tags" aria-hidden="true"></i><span class="hidden-xs hidden-sm">Calender</span></a></li>
        <li><a href="#"><i class="glyphicon glyphicon-tags" aria-hidden="true"></i><span class="hidden-xs hidden-sm">Users</span></a></li>
        <li><a href="#"><i class="glyphicon glyphicon-tags" aria-hidden="true"></i><span class="hidden-xs hidden-sm">Setting</span></a></li>
    </ul>
</div>
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