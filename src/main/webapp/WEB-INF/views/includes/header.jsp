<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page session="true"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="icon" type="image/png"
	href="${contextPath}/icons/myicon.png" />

<link rel="stylesheet"
	href="<c:url value="${contextPath}/css/style.css" />" />

<link rel="stylesheet"
	href="<c:url value="${contextPath}/css/lib/chessboard-0.3.0.css" />" />

<link rel="stylesheet"
	href="<c:url value="${contextPath}/css/lib/bootstrap.min.css" />" />
<link rel="stylesheet"
	href="<c:url value="${contextPath}/css/lib/bootstrap-theme.min.css" />" />

<link rel="stylesheet"
	href="<c:url value="${contextPath}/css/lib/jquery.dataTables.min.css" />" />

<script type="text/javascript"
	src="<c:url value="${contextPath}/js/lib/jquery-2.1.4.min.js" />">
	
</script>

<script type="text/javascript"
	src="<c:url value="${contextPath}/js/lib/bootstrap.min.js" />">
	
</script>
<script type="text/javascript"
	src="<c:url value="${contextPath}/js/lib/jquery.dataTables.min.js" />"></script>

<script type="text/javascript"
	src="<c:url value="${contextPath}/js/lib/chessboard-0.3.0.js" />"></script>

<title>iboard games</title>
</head>

<body>