<jsp:include page="includes/header.jsp" />
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
<%@ page import="com.kuzdowicz.livegaming.chess.app.models.UserAccount"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="container-fluid">
	<jsp:include page="includes/menu.jsp" />
	<div class="main-wrapper">
		<br />
		<div class="outerDiv-play">
			<div class="wrapperForKeepCenterPosition">
				<div class="site-title">
					<h4 class="text-center">Play chess with computer</h4>
				</div>
				<div class="game-stats-withcomputer">
					<p class="nice-blue-font-color">
						Status: <span id="status"></span>
					</p>
					<small class="white"> FEN: <br /> <span id="fen"></span>
					</small> <br /> <small class="text-danger"> PGN: <span id="pgn"></span>
					</small>
				</div>
				<script type="text/javascript"
					src="<c:url value="${pageContext.request.contextPath}/js/lib/chess.js" />"></script>
				<div id="chess-board-play-with-computer">
					<div id="board"></div>
					<br />
				</div>

			</div>
		</div>

	</div>
</div>
<!-- IMPORT CHESS SCRIPT -->
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/chessUserVsComputer.js">
	
</script>
<jsp:include page="includes/footer.jsp" />

