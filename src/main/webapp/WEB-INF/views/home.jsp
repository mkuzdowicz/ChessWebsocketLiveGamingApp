<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="main-wrapper">
	<br />
	<div id="home-page-wrapper">
		<div class="outerDiv">
			<div class="wrapperForKeepCenterPosition">
				<div class="site-title">
					<h4>Play board games online</h4>
				</div>
				<div id="home-img-div">
					<img id="home-img" alt="iboard home image"
						src="<c:url value="/images/konik5.jpg" />" />
				</div>

				<div id="chess-board-home">
					<div id="board"></div>
				</div>

				<div class="game-stats">
					<p class="nice-blue-font-color">
						Status: <span id="status"></span>
					</p>
					<small class="white"> FEN: <br /> <span id="fen"></span>
					</small>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="<c:url value="/js/lib/chess.js" />"></script>
<!-- IMPORT CHESS SCRIPT -->
<script type="text/javascript" src="/js/chessComputerVsComputer.js"></script>


