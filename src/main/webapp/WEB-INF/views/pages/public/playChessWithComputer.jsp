<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
				src="<c:url value="/js/lib/chess.js" />"></script>
			<div id="chess-board-play-with-computer">
				<div id="board"></div>
				<br />
			</div>

		</div>
	</div>

</div>
<!-- IMPORT CHESS SCRIPT -->
<script type="text/javascript" src="/js/pages_scripts/chessUserVsComputer.js">
	
</script>
