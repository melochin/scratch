<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<c:forEach var="anime" items="${animeList }">
	<div class="col-md-3">
	<div class="thumbnail" >
		<div class="video-img">
			<img src="${anime.key.pic }" class="img-rounded"/>
		</div>
		<div class="video-desc">
			${anime.key.name }
		</div>
		<div>
			<c:choose>
				<c:when test="${anime.value == 0 }">
					<form action="focus/add" method="post">
						<input name="animeId" type="hidden" value="${anime.key.id }" />
						<button class="btn btn-success">订阅</button>
					</form>
				</c:when>
				<c:otherwise>
					<form action="focus/delete" method="post">
						<input name="animeId" type="hidden" value="${anime.key.id }" />
						<button class="btn btn-primary">取消订阅</button>
					</form>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
	</div>
</c:forEach>

