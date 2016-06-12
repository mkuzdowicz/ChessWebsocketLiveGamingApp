<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<script src="<c:url value="/js/pages_scripts/userGamesHistory.js" />"></script>

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


	<div class="table-responsive">
		<table id="gamesTableForDataTableJS"
			class="table table-condensed table-striped table-bordered table-accounts">
			<thead>
				<tr>
					<td class="text-center" style="width: 15px !important">id</td>
					<td class="text-center">begin date</td>
					<td class="text-center">end date</td>
					<td class="text-center">game duration</td>
					<td class="text-center" style="width: 10px !important">number
						of moves</td>
					<td class="text-center" style="width: 5px !important">white
						color</td>
					<td class="text-center">black color</td>
					<td class="text-center">check mate</td>
					<td class="text-center">winner</td>
					<td class="text-center">end position</td>
				</tr>
			</thead>
			<tbody>
				<c:set var="counter" value="1"></c:set>
				<c:forEach var="chessGame" items="${userChessGames}">
					<tr>
						<td class="facebookBlue text-center white">${counter}</td>
						<td class=" text-center"><fmt:formatDate pattern="dd-MM-yyyy"
								value="${chessGame.beginDate}" /></td>
						<td class=" text-center"><fmt:formatDate pattern="dd-MM-yyyy"
								value="${chessGame.endDate}" /></td>
						<td class="text-center">${chessGame.formattedGameDurationStr}</td>
						<td class=" text-center">${chessGame.numberOfMoves}</td>
						<td class=" text-center">${chessGame.whitePlayerName}</td>
						<td class=" text-center">${chessGame.blackPlayerName}</td>
						<c:choose>
							<c:when test="${chessGame.checkMate eq true}">
								<td class="text-center nice-orange-bg-color white">yes</td>
							</c:when>
							<c:otherwise>
								<td class=" text-center">no</td>
							</c:otherwise>
						</c:choose>

						<td class=" text-center"><c:if
								test="${chessGame.winnerName ne null}">${chessGame.winnerName}</c:if>
						</td>
						<td class=" text-center">
							<button class="btn btn-success btn-block showEndPsoBtn"
								data-fen="${chessGame.endingGameFENString}">show end
								position</button>
						</td>

					</tr>
					<c:set var="counter" value="${counter +1}"></c:set>
				</c:forEach>
			</tbody>
		</table>
	</div>


</div>
<jsp:include
	page="includes/modal_boxes/user_end_chess_game_pos_modal.jsp" />

