<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://tiles.apache.org/tags-tiles" prefix="t"%>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<t:insertAttribute name="lib" ignore="true"/>
		<title>
			<t:insertAttribute name="title" ignore="true"/>
		</title>
	</head>
	<body>
		<div class="display-table">
			<div class="navbar navbar-default navbar-fixed-top topbar">
				<t:insertAttribute name="header"/>
			</div>
			<div class="wrapper toggled">
				<div class="sidebar">
						<t:insertAttribute name="nav" />
				</div>
				<div class="main">
					<t:insertAttribute name="content"/>
				</div>
			</div>
		</div> 
	</body>
</html>
