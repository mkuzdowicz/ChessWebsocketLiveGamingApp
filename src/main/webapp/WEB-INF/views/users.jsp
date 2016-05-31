<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="main-wrapper">

	<%@ page import="java.util.List"%>
	<%@ page import="com.kuzdowicz.livegaming.chess.app.models.UserAccount"%>
	<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
	<%@ page import="com.kuzdowicz.livegaming.chess.app.props.*"%>
	<%
		String contextURL = ChessAppProperties.getProperty("app.contextpath");
	%>

	<a id="add-new-user-btn" class="btn btn-primary btn-block"
		href="<%=contextURL%>/admin/users/addUser">add new user</a>
	<div id="usersTable" class="tab-pane fade in active">

		<h3 class="text-center">
			<span class="glyphicon glyphicon-user"></span> USERS <span
				class="glyphicon glyphicon-user"></span>
		</h3>
		<jsp:include page="includes/tables/usersTable.jsp"></jsp:include>
	</div>
</div>
<jsp:include page="includes/modal_boxes/removeUserModal.jsp"></jsp:include>

