<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<script type="text/babel" src="<c:url value="/js/react-tag-video.js"/>" ></script>
<nav class="navbar-container">
	<div class="container">
    <!-- Brand and toggle get grouped for better mobile display -->
	    <div class="navbar-header">
	      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse-2">
	        <span class="sr-only">Toggle navigation</span>
	      </button>
	    </div>
    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="navbar-collapse-2">
    	<div id="search" class="col-md-6" style="padding-top:8px;">
    	</div>
    <div>
	<ul class="nav navbar-nav navbar-right">
	<li>
	      	<a><span class="glyphicon glyphicon-bell"></span></a>
	      </li>
	      <li>
	          <c:if test="${user_g != null }">
	        	<a href="" class="dropdown-toggle" data-toggle="dropdown">
	        		${user_g.username }
	    			<b class="caret" ></b>
	        	</a>
	        	<ul class="dropdown-menu">
	        		<li id="user_logout">
	        			<div class="row">
	        				<div class="col-md-12">
	        					<div class="text-center">
	        						<span>:)</span>
	        					</div>
	        					<c:if test="${user_g.role == 1 }">
	        					<div class="text-center">
	        						<b><a href="<c:url value="/bili/setting"/>">admin</a></b>
	        					</div>
	        					</c:if>
	        					<div class="text-center">
	        						<b><a href="<c:url value="/user/logout"/>">log out</a></b>
	        					</div>
	        				</div>
	        			</div>
	        		</li>
	        	</ul>
	    	</c:if>
	   		<c:if test="${user_g == null }">
	   			<a class="btn btn-default btn-outline btn-circle collapsed"   
	    			href="<c:url value="/user/login" />" aria-expanded="false" aria-controls="nav-collapse2">Sign in</a>
	   		</c:if>
	    </li>
	  </ul>
    </div>
    </div>
  </div>
</nav>