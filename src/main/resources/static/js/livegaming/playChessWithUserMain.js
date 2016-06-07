/**
 * user vs user main
 */

var SENDED_CHESS_MOVE_STATUS = "";

var CURRENT_CHESS_MOVE = "";

var CHESS_MOVE_COUNTER = 0;

var WHITE_COLOR_USERNAME = "";

var BLACK_COLOR_USERNAME = "";

var OPPONENT_USERNAME = "";

var SEND_MOVE_CLICK_COUNTER = 0;

$(function() {

	$('#game-status').data('isPlaying', false);

	$('#connectToWebSocket').click(function(event) {
		connectToWebSocket();
	});

	$('#disconnect').attr("disabled", true);

	$('#disconnect').click(function() {
		closeWsConnection();
	});

	$('#quit-game-btn').click(function() {
		quitGame();
	});

	$('#undo-move-btn').click(function() {
		undoMove();
	});

	$('#send-move-btn').click(function() {

		if (CHESS_MOVE_COUNTER == 0) {
			alert('You have to make a move before you send it');
			return;
		}
		
		if (SEND_MOVE_CLICK_COUNTER != 0) {
			alert('You have already send your move');
			return;
		}

		sendYourMoveByFenNotationToUser();
		showActualMoveStatus();
	});

});