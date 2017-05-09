<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script type="text/javascript">
	$(document).ready(function() {
		//setTimeout("jump()", 5000);
	});
		
	function jump() {
		var url = "${jump }";
		if(url == null || url == "") {
			//default use histroy
			history.go(-1);	
		} else {
			//if specified use url
			//window.location = url;
			window.location = new URL(url, document.location);
		}
	}

</script>

<div class="row">
	<div class="col-md-2"></div>
		<div class="col-md-8 msg">
			<c:choose>
				<c:when test="${success != null }">
					<div id="messBox" class="alert alert-success alert-autocloseable-info">
						${success }
					</div>
				</c:when>
				<c:otherwise>
					<div id="messBox" class="alert alert-danger alert-autocloseable-info">
						${error }
						<c:if test="${errors != null}">
							<ul>
								<c:forEach var="e" items="${errors }">
									<li>${e }</li>
								</c:forEach>				
							</ul>
						</c:if>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	<div class="col-md-2"></div>
</div>
