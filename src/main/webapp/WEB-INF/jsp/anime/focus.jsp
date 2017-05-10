
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="row">
	<div class="col-md-6 anime-focus-filter">
		<div class="row">
			<div class="col-md-2">
				<label>类型：</label>
			</div>
			<div class="col-md-10">
				<div class="row">
					<div class="col-md-2">
						<c:choose>
							<c:when test="${type == null }">
								<a class="choose" href="focus?focus=${focus}">全部</a>
							</c:when>
							<c:otherwise>
								<a href="focus?focus=${focus}">全部</a>
							</c:otherwise>
						</c:choose>
					</div>
					<c:forEach var="animeType" items="${animeTypes.entrySet() }">
						<div class="col-md-2">
							<c:choose>
								<c:when test="${type == animeType.key }">
									<a class="choose" href="focus?type=${animeType.key }&focus=${focus}">${animeType.value }</a>
								</c:when>
								<c:otherwise>
									<a href="focus?type=${animeType.key }&focus=${focus}">${animeType.value }</a>
								</c:otherwise>
							</c:choose>
						</div>
					</c:forEach>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-2">
				<label>订阅：</label>
			</div>
			<div class="col-md-10">
				<div class="row">
					<div class="col-md-2">
						<c:choose>
							<c:when test="${focus == null }">
								<a class="choose" href="focus?type=${type }">全部</a>
							</c:when>
							<c:otherwise>
								<a href="focus?type=${type }">全部</a>
							</c:otherwise>
						</c:choose>
					</div>
					<div class="col-md-2">
						<c:choose>
							<c:when test="${focus == 1 }">
								<a class="choose" href="focus?type=${type }&focus=1">订阅中</a>
							</c:when>
							<c:otherwise>
								<a href="focus?type=${type }&focus=1">订阅中</a>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="row anime-focus">
<c:forEach var="anime" items="${animeList }">
	<div class="col-md-3">
		<div class="thumbnail">
			<div class="video-img">
				<img src="${anime.key.pic }" class="img-rounded" />
			</div>
			<div class="video-desc">${anime.key.name }</div>
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
</div>