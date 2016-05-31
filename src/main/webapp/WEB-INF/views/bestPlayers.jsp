<jsp:include page="includes/header.jsp" />
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="container-fluid">
	<jsp:include page="includes/menu.jsp" />
	<div class="main-wrapper ">

		<%@ page import="java.util.List"%>
		<%@ page import="com.kuzdowicz.livegaming.chess.app.models.UserAccount"%>
		<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
		<%@ page import="com.kuzdowicz.livegaming.chess.app.props.*"%>
		<%
			String contextURL = ChessAppProperties
					.getProperty("app.contextpath");
		%>
		<script
			src="<c:url value="${pageContext.request.contextPath}/js/main.js" />"></script>
		<script
			src="<c:url value="${pageContext.request.contextPath}/js/lib/jquery.canvasjs.min.js" />"></script>
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
		<script
			src="<c:url value="${pageContext.request.contextPath}/js/bestPlayersMain.js" />"></script>

	</div>
</div>
<jsp:include page="includes/footer.jsp" />