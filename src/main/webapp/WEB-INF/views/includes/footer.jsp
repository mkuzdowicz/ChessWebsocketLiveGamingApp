<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.kuzdowicz.livegaming.chess.app.props.*"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
	String version = ChessAppProperties.getProperty("chessApp.version");
	String appMail = ChessAppProperties.getProperty("mail.username");
%>
<br />
<footer class="footer">
	<span class="author">author: Marcin Kużdowicz</span> <span
		class="appTitle"><%=appMail%></span> <span class="pull-right">version:
		<%=version%></span>
</footer>
<script type="text/javascript"
	src="<c:url value="${pageContext.request.contextPath}/static/resources/js/main.js" />"></script>
</body>
</html>