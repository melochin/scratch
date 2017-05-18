<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page isELIgnored="false"%>
<script type="text/babel" src="<c:url value="/js/react-tag-video.js"/>"></script>
<nav class="navbar-container">
	<div class="nav-wrapper">
		<a href="#" class="brand-logo">Scratch</a>
		<ul id="nav-mobile" class="right hide-on-med-and-down">
			<li><c:if test="${user_g != null }">
					<a href="" class="dropdown-toggle dropdown-button"
						data-toggle="dropdown" data-activates="user-ops">
						${user_g.username } <b class="caret"></b>
					</a>
				</c:if> <c:if test="${user_g == null }">
					<a class="btn btn-default btn-outline btn-circle collapsed"
						href="<c:url value="/user/login" />" aria-expanded="false"
						aria-controls="nav-collapse2">Sign in</a>
				</c:if></li>
		</ul>
	</div>
</nav>

<ul id="user-ops" class="dropdown-content">
	<c:if test="${user_g.role == 1 }">
		<li><a href="<c:url value="/admin"/>">admin</a></li>
		<li class="divider"></li>
	</c:if>
	<li><a href="<c:url value="/user/logout"/>">log out</a></li>
</ul>