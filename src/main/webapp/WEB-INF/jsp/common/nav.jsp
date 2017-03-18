<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
    <div>
        <ul class="sidebar-nav">
            <li><a href="<c:url value="/"/>"><i class="glyphicon glyphicon-home" aria-hidden="true"></i><span class="hidden-xs hidden-sm">Home</span></a></li>
            <li><a href='<c:url value="/search/tag"/>'><i class="glyphicon glyphicon-tags" aria-hidden="true"></i><span class="hidden-xs hidden-sm">Tag</span></a></li>
            <li><a href="#"><i class="glyphicon glyphicon-tags" aria-hidden="true"></i><span class="hidden-xs hidden-sm">Statistics</span></a></li>
            <li><a href="#"><i class="glyphicon glyphicon-tags" aria-hidden="true"></i><span class="hidden-xs hidden-sm">Calender</span></a></li>
            <li><a href="#"><i class="glyphicon glyphicon-tags" aria-hidden="true"></i><span class="hidden-xs hidden-sm">Users</span></a></li>
            <li><a href="#"><i class="glyphicon glyphicon-tags" aria-hidden="true"></i><span class="hidden-xs hidden-sm">Setting</span></a></li>
            <li style="height:100%"></li>
        </ul>
    </div>
<script>
$(document).ready(function() {
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
	var list = $(".navi a");
	for(var i=0; i<list.length; i++) {
		var a = $(list.get(i));
		if(a.attr("href") == curUrl) {
			return a.parent("li");
		}
	}
	return null;
}
</script>