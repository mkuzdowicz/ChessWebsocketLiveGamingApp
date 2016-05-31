<jsp:include page="includes/header.jsp" />
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="container-fluid">
	<jsp:include page="includes/menu.jsp" />
	<div class="main-wrapper">
		<%@ page import="java.util.List"%>
		<%@ page import="com.kuzdowicz.livegaming.chess.app.models.UserAccount"%>
		<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
		<%@ page import="com.kuzdowicz.livegaming.chess.app.props.*"%>
		<%
			String contextURL = ChessAppProperties
					.getProperty("app.contextpath");
		%>
		<script
			src="<c:url value="${pageContext.request.contextPath}/resources/js/userGamesHistory.js" />"></script>

		<div id="user-games-stats" class="text-center nice-green-backgroud">
			<table class="table table-condensed">
				<c:if test="${user.numberOfGamesPlayed ne null}">
					<tr>
						<td><span class="glyphicon glyphicon-asterisk"></span></td>
						<td><span class="white text-uppercase">number of games
								palyed : </span></td>
						<td><span class="number-of-games">${user.numberOfGamesPlayed}</span></td>
					</tr>
				</c:if>
				<c:if test="${user.numberOfWonChessGames ne null}">
					<tr>
						<td><span class="glyphicon glyphicon-star-empty"></span></td>
						<td><span class="nice-blue-font-color text-uppercase">number
								of won games : </span></td>
						<td><span class="number-of-games">${user.numberOfWonChessGames}</span></td>
					</tr>
				</c:if>
				<c:if test="${user.numberOfLostChessGames ne null}">
					<tr>
						<td><span class="glyphicon glyphicon-thumbs-down"></span></td>
						<td><span class="text-danger text-uppercase">number of
								lost games : </span></td>
						<td><span class="number-of-games">${user.numberOfLostChessGames}</span></td>
					</tr>
				</c:if>
			</table>
		</div>

		<div id="gamesTable">
			<h3 class="text-center">
				<span class="glyphicon glyphicon-info-sign"></span> Your chess games
			</h3>
			<jsp:include page="includes/tables/userGamesTable.jsp"></jsp:include>
		</div>
	</div>
</div>
<jsp:include
	page="includes/modal_boxes/user_end_chess_game_pos_modal.jsp" />

<jsp:include page="includes/footer.jsp" />