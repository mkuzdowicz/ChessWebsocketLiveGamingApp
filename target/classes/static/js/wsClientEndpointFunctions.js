/**
 * functions for websocket client endpoint
 */
function testCheckMateSaveToDb() {

	var checkMatePos = "4kb2/1pp1ppp1/5n2/r4K1b/3q4/PPP5/R2P1PP1/2B1r3 w - - 7 20";
	fenFromYourMove.value = checkMatePos;
	board.position(checkMatePos);
	game = new Chess(checkMatePos);

}
// -------------------------------------------------------

function undoMove() {
	console.log('undoMove()');

	CHESS_MOVE_COUNTER = 0;
	var prevoiusPos = fenFromPreviousMove.value;
	board.position(prevoiusPos);
	game = new Chess(prevoiusPos);
	updateStatus();

}

// -----------------------------------------------------

function setChessColorGlobalVars(msgObj) {
	console.log('setChessColorGlobalVars()');

	if (msgObj.sendToObj.chessColor == 'white') {
		WHITE_COLOR_USERNAME = msgObj.sendToObj.username;
		BLACK_COLOR_USERNAME = msgObj.sendFromObj.username;
	} else {
		WHITE_COLOR_USERNAME = msgObj.sendFromObj.username;
		BLACK_COLOR_USERNAME = msgObj.sendToObj.username;
	}

}

// -----------------------------------------------------

function showGameHandshakeModalBox(sender) {

	var modalBoxMsg = "Do you want to play with: "
			+ "<span class=\"text-primary\"><b>" + sender + "</b></span>";

	$('#game-handshake-modal-title').html(modalBoxMsg);
	$('#game-handshake-msgTo').val(sender);
	$('#game-handshake-modal').modal('show');

}

function showTimerForInviter(recieverName) {
	console.log('showTimerForInviter()');

	if (TIMEOUT_FOR_HANDSHAKE == 0 || CLICK_REFUSED_FLAG == true
			|| CLICK_AGREEMENT_FLAG == true) {

		setTimeOutForHandshake_RefuseFlag_AgreementFlag_ForStartValues();
		clearUserInetrfaceForTimer();
		clearTimeout(inviterTimer);
		return;
	} else {
		TIMEOUT_FOR_HANDSHAKE--;

		var timerInfo = '<div class="alert alert-info">' + '<span><strong>'
				+ recieverName + '</strong> considering... <strong>'
				+ TIMEOUT_FOR_HANDSHAKE + '</strong></span></div>';

		$('#game-status').html(timerInfo);
	}

	var inviterTimer = setTimeout(function() {
		showTimerForInviter(recieverName)
	}, 1000);

}

function setTimeOutForHandshake_RefuseFlag_AgreementFlag_ForStartValues() {
	console
			.log("setTimeOutForHandshake_RefuseFlag_AgreementFlag_ForStartValues()");

	TIMEOUT_FOR_HANDSHAKE = 15;
	CLICK_REFUSED_FLAG = false;
	CLICK_AGREEMENT_FLAG = false;
}

function startTimeoutForHandshakeForInvitedUser() {
	console.log('setChessColorGlobalVars()');

	if (CLICK_REFUSED_FLAG == true || CLICK_AGREEMENT_FLAG == true) {

		setTimeOutForHandshake_RefuseFlag_AgreementFlag_ForStartValues();

		clearTimeout(reciverTimer);
		return;
	}

	if (TIMEOUT_FOR_HANDSHAKE == 0) {

		refusedToPlay();
		setTimeOutForHandshake_RefuseFlag_AgreementFlag_ForStartValues();
		clearTimeout(reciverTimer);
		return;
	} else {
		TIMEOUT_FOR_HANDSHAKE--;
		$('#game-handshake-timer').html(TIMEOUT_FOR_HANDSHAKE);
	}

	var reciverTimer = setTimeout(function() {
		startTimeoutForHandshakeForInvitedUser()
	}, 1000);

}

// -----------------------------------

function clearParticipantsListView() {
	console.log("clearParticipantsListView()");

	var participantListUsernameBtns = $('#participants div ul li button.username');

	participantListUsernameBtns.each(function() {

		if ($(this).text().trim() == WEBSOCKET_CLIENT_NAME) {

			$(this).parent().find('span.participants-action-btns').remove();
			$(this).parent().css('background-color', '#C9C9C9');
		}

		if ($('#game-status').data('isPlaying') == true) {
			$(this).parent().find('span.participants-action-btns').attr(
					'hidden', true);
		} else {
			$(this).parent().find('span.participants-action-btns').attr(
					'hidden', false);
		}

	});
}

// -----------------------------------

function inviteUserToGame(reciever) {
	console.log("inviteUserToGame()");
	console.log("game-handshake from " + WEBSOCKET_CLIENT_NAME);
	console.log(" to " + reciever);

	// clear
	CLICK_REFUSED_FLAG = false;
	CLICK_AGREEMENT_FLAG = false;
	TIMEOUT_FOR_HANDSHAKE = 15;

	CHESS_MOVE_COUNTER = 0;
	webSocket.send(JSON.stringify({
		type : "game-handshake-invitation",
		sendFrom : WEBSOCKET_CLIENT_NAME,
		sendTo : reciever
	}));

	showTimerForInviter(reciever);

}

// --------------------------------------------------------

function showActualMoveStatus() {
	console.log("showActualMoveStatus");

	if (SENDED_CHESS_MOVE_STATUS == $('#status').text().trim()) {
		$('#move-for')
				.html(
						'<p class=\"move-for-p text-success text-center nice-green-backgroud white\">Your move</p>');
	} else {
		$('#move-for')
				.html(
						'<p class=\"move-for-p text-danger text-center bg-danger\">Move for opponent</p>');
	}

}

// --------------------------------------------------------

function agreementToPlay() {
	console.log("agreementToPlay()");

	CLICK_AGREEMENT_FLAG = true;

	var usernameToPlayWith = $('#game-handshake-msgTo').val();
	OPPONENT_USERNAME = usernameToPlayWith;
	var myUserName = WEBSOCKET_CLIENT_NAME;
	console.log("game-handshake-agreement from");
	console.log(myUserName + " with " + usernameToPlayWith);
	var msg = "agreement";
	webSocket.send(JSON.stringify({
		type : "game-handshake-agreement",
		handshakeMsg : msg,
		sendFrom : WEBSOCKET_CLIENT_NAME,
		sendTo : usernameToPlayWith
	}));

	$('#game-status').data('isPlaying', true);
	$('#startPosBtn').hide();
	$('#fenFromPreviousMove').val(startFENPosition);

	$('#play-with-opponent-interface').attr("hidden", false);
	$('#play-with-opponent-interface-actions').attr("hidden", false);

	var alertMessageYouArePlayingWith = "<div id=\"you-are-playing-with-info\" class=\"alert nice-red-bg-color text-center white\">"
			+ "<p>you are playing now with: <strong>"
			+ usernameToPlayWith
			+ "</strong></p>" + "</div>";

	$('#game-status').html(alertMessageYouArePlayingWith);
	$('#send-move-btn').data("opponentName", usernameToPlayWith);
	$('#quit-game-btn').data("gamePartner", usernameToPlayWith);

	showActualMoveStatus();
	startNewGame();

	$('#game-handshake-modal').modal('hide');

}

// --------------------------------------------------------

function quitGame() {

	var endFen = fenFromYourMove.value;
	CHESS_MOVE_COUNTER = 0;

	webSocket.send(JSON.stringify({
		type : "quit-game",
		whiteColUsername : WHITE_COLOR_USERNAME,
		blackColUsername : BLACK_COLOR_USERNAME,
		fen : endFen,
		sendFrom : WEBSOCKET_CLIENT_NAME,
		sendTo : $('#quit-game-btn').data("gamePartner")
	}));

	$('#game-status').data('isPlaying', false);
	$('#startPosBtn').show();
	$('#play-with-opponent-interface').attr("hidden", true);
	$('#game-status').html('');
	$('#opponent-username').html('');
	$('#send-move-btn').data("opponentName", '');
	$('#quit-game-btn').data("gamePartner", '');

	OPPONENT_USERNAME = "";

}

// --------------------------------------------------------

function refusedToPlay() {
	console.log("refusedToPlay()");

	CLICK_REFUSED_FLAG = true;
	var usernameNotToPlayWith = $('#game-handshake-msgTo').val();
	var myUserName = WEBSOCKET_CLIENT_NAME;

	console.log("game-handshake-refuse from");
	console.log(myUserName + " to " + usernameNotToPlayWith);

	webSocket.send(JSON.stringify({
		type : "game-handshake-refuse",
		sendFrom : WEBSOCKET_CLIENT_NAME,
		sendTo : usernameNotToPlayWith
	}));

	clearUserInetrfaceForTimer();

}

// -------------------------------------------------------

function clearUserInetrfaceForTimer() {
	$('#game-handshake-modal').modal('hide');
}

// --------------------------------------------------------

function sendYourMoveByFenNotationToUser() {

	SEND_MOVE_CLICK_COUNTER = 1;
	console.log("sendYourMoveByFenNotationToUser()");
	console.log("current chess move");
	console.log(CURRENT_CHESS_MOVE);

	var reciever = $('#send-move-btn').data('opponentName');

	var chessMoveStatus = $('#status').text().trim();

	console.log("send-fen : " + fenFromYourMove.value);
	console.log(" to " + reciever);
	console.log(" from " + WEBSOCKET_CLIENT_NAME);
	var fenString = fenFromYourMove.value;
	webSocket.send(JSON.stringify({
		type : "chess-move",
		fen : fenString,
		moveStatus : chessMoveStatus,
		chessMove : CURRENT_CHESS_MOVE,
		sendFrom : WEBSOCKET_CLIENT_NAME,
		sendTo : reciever
	}));

};

// -----------------------------------------------
function closeWsConnection() {
	console.log('closeWsConnection()');
	$('.connectToUserBtn').css("color", "white");
	$('#game-status').data('isPlaying', false);
	OPPONENT_USERNAME = "";
	webSocket.close();
};

// -----------------------------------------------------------

function showParticipants(data) {
	console.log("showParticipants()");

	var participantsArr = JSON.parse(data);
	var participantsList = $('#participants div ul');
	participantsList.html('');

	for (var i = 0; i < participantsArr.length; i++) {

		var liElementOpening = '<li class="list-group-item game-user">';
		var participantData = "";

		if (participantsArr[i].communicationStatus == 'is-playing') {

			participantData = '<button class="username btn"'
					+ 'onclick="showUSerInfoByAjax(' + '\''
					+ participantsArr[i].username + '\'' + ')"' + '>'
					+ '<span class="glyphicon glyphicon-user text-danger" />'
					+ '&nbsp;' + participantsArr[i].username + '</button>'
					+ '<span class="small"> ' + '&nbsp;'
					+ participantsArr[i].communicationStatus + ' </span>'
					+ '<span class="participant-timer"></span>';
		} else {
			participantData = '<button class="username btn"'
					+ 'onclick="showUSerInfoByAjax(' + '\''
					+ participantsArr[i].username + '\'' + ')"' + '>'
					+ '<span class="glyphicon glyphicon-user text-success" />'
					+ '&nbsp;' + participantsArr[i].username + '</button>'
					+ '<span class="small text-info"> ' + '&nbsp;'
					+ participantsArr[i].communicationStatus + ' </span>'
					+ '<span class="participant-timer"></span>';
		}

		var participantPlayWithUserInfo = "";

		if (participantsArr[i].playNowWithUser != undefined
				&& participantsArr[i].playNowWithUser != '') {
			participantPlayWithUserInfo = '<span class="small text-success"> with <b>'
					+ participantsArr[i].playNowWithUser + '</b></span>';
		}

		var participantActionBtns = "";

		if (participantsArr[i].communicationStatus == 'wait-for-new-game') {

			participantActionBtns = '<span class="participants-action-btns">'
					+ '<button class="btn btn-sm btn-danger send-to-user-btn" onclick="inviteUserToGame('
					+ '\'' + participantsArr[i].username + '\'' + ')"'
					+ 'data-username="' + participantsArr[i].username
					+ '">invite</button>' + '</span>';

		}

		var liElementClosing = '</li>';

		var participantListElementContent = liElementOpening + participantData
				+ participantPlayWithUserInfo + participantActionBtns
				+ liElementClosing;

		participantsList.append(participantListElementContent);
	}
};

// ----------------------------------------------

function showUSerInfoByAjax(login) {
	$('#user-info-modal').modal('show');
	$.ajax({
		url : "user/get-user-info-by-username",
		data : {
			username : login
		},
		success : function(response) {
			$('#usernameResponse').text("login: " + response.username);
			$('#userIdResponse').text("id: " + response.userId);
			$('#userEmailResponse').text("email: " + response.email);
			$('#userNameResponse').text("name: " + response.name);
			$('#userLastnameResponse').text("lastname: " + response.lastname);
		}
	});
};

