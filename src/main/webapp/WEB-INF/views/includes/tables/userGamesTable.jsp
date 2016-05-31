<%@ page import="com.kuzdowicz.livegaming.chess.app.props.*"%>
<%
	String contextURL = ChessAppProperties
			.getProperty("app.contextpath");
%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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
					<td class=" text-center">${chessGame.whiteColUsername}</td>
					<td class=" text-center">${chessGame.blackColUsername}</td>
					<c:choose>
						<c:when test="${chessGame.checkMate eq true}">
							<td class="text-center nice-orange-bg-color white">yes</td>
						</c:when>
						<c:otherwise>
							<td class=" text-center">no</td>
						</c:otherwise>
					</c:choose>

					<td class=" text-center"><c:if
							test="${chessGame.winnerUsername ne null}">${chessGame.winnerUsername}</c:if>
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