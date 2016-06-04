<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="main-wrapper ">

	<%@ page import="java.util.List"%>
	<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
	<script src="<c:url value="/js/main.js" />"></script>
	<script src="<c:url value="/js/lib/jquery.canvasjs.min.js" />"></script>
	<br />
	<div class="site-title">
		<h1 class="text-center nice-green-backgroud">
			<span class="glyphicon glyphicon-stats"></span> Best 10 players
		</h1>
	</div>
	<div id="bestChessGamersChart" style="height: 400px; width: 100%;"></div>

	<c:set var="usersJson" value="${bestPlayersJson}"></c:set>

	<script type="text/javascript">
		var USERS_JSON_ARR = JSON.parse('${usersJson}');
	</script>
	<script src="<c:url value="/js/bestPlayersMain.js" />"></script>

</div>
