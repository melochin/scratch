<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<nav class="navbar-container">
      <div class="container">
        <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse-2">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
<!--       <a class="navbar-brand" href="#">Tool</a> -->
    </div>
    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="navbar-collapse-2">
      <ul class="nav navbar-nav navbar-right">
<!--         <li>
        	<a><span class="glyphicon glyphicon-envelope"></span></a>
        </li> -->
        <li>
        	<!-- when user logged -->
            <c:if test="${user_g != null }">
            	<a href="" class="dropdown-toggle" data-toggle="dropdown">
            		${user_g.username }
	            	<%-- <img src="" alt="${user_g.username }" /> --%>
	       			<b class="caret" ></b>
            	</a>
            	<ul class="dropdown-menu">
            		<li id="user_logout">
            			<div class="row">
            				<div class="col-md-12">
            					<div class="text-center">
            						<span>:)</span>
            					</div>
            					<div class="text-center">
            						<b><a href="<c:url value="/user/logout"/>">log out</a></b>
            					</div>
            				</div>
            			</div>
            		</li>
            	</ul>
        	</c:if>
       		<c:if test="${user_g == null }">
       			<!-- data-toggle="collapse" -->
       			<a class="btn btn-default btn-outline btn-circle collapsed"   
 	      			href="<c:url value="/user/login" />" aria-expanded="false" aria-controls="nav-collapse2">Sign in</a>
       		</c:if>
        </li>
      </ul>
      <div class="collapse nav navbar-nav nav-collapse slide-down" id="nav-collapse3">
        <form class="navbar-form navbar-right form-inline" role="form">
          <div class="form-group">
            <label class="sr-only" for="Email">Email</label>
            <input type="email" class="form-control" id="Email" placeholder="Email" autofocus required />
          </div>
          <div class="form-group">
            <label class="sr-only" for="Password">Password</label>
            <input type="password" class="form-control" id="Password" placeholder="Password" required />
          </div>
          <button type="submit" class="btn btn-success">Sign in</button>
        </form>
      </div>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container -->
</nav><!-- /.navbar -->