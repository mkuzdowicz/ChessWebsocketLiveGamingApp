<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.kuzdowicz.livegaming.chess.app.props.*"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
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
</body>
</html>