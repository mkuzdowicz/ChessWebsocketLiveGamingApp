<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page session="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" type="image/png" href="/icons/myicon.png" />
<link rel="stylesheet" href="<c:url value="/css/style.css" />" />
<link rel="stylesheet"
	href="<c:url value="/css/lib/chessboard-0.3.0.css" />" />
<link rel="stylesheet"
	href="<c:url value="/css/lib/bootstrap.min.css" />" />
<link rel="stylesheet"
	href="<c:url value="/css/lib/bootstrap-theme.min.css" />" />
<link rel="stylesheet"
	href="<c:url value="/css/lib/jquery.dataTables.min.css" />" />
<script type="text/javascript"
	src="<c:url value="/js/lib/jquery-2.1.4.min.js" />">
	
</script>
<script type="text/javascript"
	src="<c:url value="/js/lib/bootstrap.min.js" />">
	
</script>
<script type="text/javascript"
	src="<c:url value="/js/lib/jquery.dataTables.min.js" />"></script>
<script type="text/javascript"
	src="<c:url value="/js/lib/chessboard-0.3.0.js" />"></script>

<title>iboard games</title>
</head>

<body>
	<div class="container-fluid">
		<ul id="navButtons" class="nav nav-tabs navView">
			<li><a href="/">home</a></li>
			<li><a href="/play-chess-with-computer">play-with-computer</a></li>
			<li><a href="/play-chess-with-user">play-with-others</a></li>

			<security:authorize access="hasRole('ROLE_ADMIN')">
				<li><a href="/admin/users">all-users</a></li>
			</security:authorize>

			<security:authorize access="hasRole('ROLE_ADMIN')" var="isAdmin" />

			<security:authorize access="hasRole('ROLE_USER')">
				<li><a href="/user/your-account">your-profile</a></li>
			</security:authorize>

			<security:authorize access="hasRole('ROLE_USER')">
				<li><a href="/user/your-chessgames">your-games-history</a></li>
			</security:authorize>
			<li><a href="/home/best-players">best-players</a></li>
			<security:authorize access="!isAnonymous()">
				<li class="pull-right logOutBtn ">
					<form class="form-inline" action="/logout" method="post">
						<input type="hidden" name="${_csrf.parameterName}"
							value="${_csrf.token}" />
						<button class="btn navbar-btn" type="submit">logout</button>
					</form>
				</li>
			</security:authorize>
			<security:authorize access="isAnonymous()">
				<li class="pull-right logInBtn"><a href="/login">log in</a></li>
			</security:authorize>
		</ul>
		<div class="welcome-user-line">
			<div class="col-md-8">
				<h3 class="text-left bright-font-color header-title">
					<span class="glyphicon glyphicon glyphicon-king"></span> Iboard
					games
				</h3>
			</div>
			<security:authorize access="hasAnyRole('ROLE_ADMIN, ROLE_USER')">
				<div class="col-md-4">
					<h4 class="text-right logged-user-name">
						Welcome: <span class="main-active-font-color"><b>${currentUserName}</b></span>
					</h4>
				</div>

			</security:authorize>
		</div>

		<sitemesh:write property='body' />

		<!-- CONTENT END -->

		<br />
		<footer class="footer">
			<div class="text-center">
				author:&nbsp;
				<spring:message code="author" />
				&nbsp;&nbsp;&nbsp;&nbsp; version:&nbsp;
				<spring:message code="version" />

			</div>
		</footer>
		<script type="text/javascript" src="<c:url value="/js/main.js" />"></script>

	</div>
</body>
</html>