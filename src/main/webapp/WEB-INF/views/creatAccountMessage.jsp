<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.kuzdowicz.livegaming.chess.app.props.*"%>
<%
	String contextURL = ChessAppProperties
			.getProperty("app.contextpath");
%>

<jsp:include page="includes/header.jsp" />
<div class="container-fluid">
	<jsp:include page="includes/menu.jsp"></jsp:include>
	<div class="main-wrapper">

		<br />
		<div class="message-box">

			<c:choose>
				<c:when test="${created}">

					<h2 class="alert alert-success text-center user-create-msg">${msg}</h2>

					<h3 class="text-center">Thanks for signing up!</h3>
					<p class="text-center">
						to&nbsp;<span class="text-info"><b>${userMail}</b></span>
					</p>
					<p class="text-center">was sent an activation link</p>
					<p class="text-center">To activate your account, click on the
						the activation link in your email</p>

				</c:when>
				<c:otherwise>
					<h2 class="alert alert-danger text-center user-create-msg">${msg}</h2>
					<a class="btn btn-success" href="<%=contextURL%>/signin">sign
						in</a>
				</c:otherwise>

			</c:choose>

		</div>

	</div>
</div>
<jsp:include page="includes/footer.jsp" />