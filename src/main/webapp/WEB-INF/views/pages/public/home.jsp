<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<br />

<div class="row">
	<div class="col-md-5 col-md-offset-1">
		<img id="home-img" alt="iboard home image"
			src="<c:url value="/images/konik5.jpg" />" />
	</div>

	<div class="col-md-5">
		<div id="board"></div>
	</div>
</div>

<div class="row">
	<br>
</div>

<div class="row">
	<div class="col-md-12">
		<div class="game-stats">
			<p class="nice-blue-font-color">
				Status: <span id="status"></span>
			</p>
			<small class="white"> FEN: <br /> <span id="fen"></span>
			</small>
		</div>
	</div>

</div>
<script type="text/javascript" src="<c:url value="/js/lib/chess.js" />"></script>
<!-- IMPORT CHESS SCRIPT -->
<script type="text/javascript"
	src="/js/pages_scripts/chessComputerVsComputer.js"></script>


