/**
 * html objects on home page:
 * 
 * div#board, div#status, div#fen, div#pgn
 */

var startFENPosition = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

// load game object with fen
// $('#setFenBtn').click(function() {
// var fenStr = $('#fenToSet').val().trim();
// console.log("fenStr " + fenStr);
// board.position(fenStr);
// game = new Chess(fenStr);
// updateStatus();
//
// });

// start new game
$('#startPosBtn').click(function() {
	startNewGame();
});

function startNewGame() {
	board.position(startFENPosition);
	game = new Chess(startFENPosition);
	updateStatus();
}

// chess script ---------------------------------
var board, game = new Chess(), statusEl = $('#status'), fenEl = $('#fen'), pgnEl = $('#pgn');

var onDragStart = function(source, piece, position, orientation) {
	console.log("onDragStart()");
	console.log('SENDED_CHESS_MOVE_STATUS: ' + SENDED_CHESS_MOVE_STATUS);

	// if move does not belong to you
	// pieces are blocked
	// inviting user at start have "white piece"
	// at start status is "White to move"
	if (statusEl.text().trim() != SENDED_CHESS_MOVE_STATUS) {
		return false;
	}

	// allow only one move
	if (CHESS_MOVE_COUNTER > 0) {
		return false;
	}

	// do not pick up pieces if the game is over
	// only pick up pieces for the side to move
	if (game.game_over() === true
			|| (game.turn() === 'w' && piece.search(/^b/) !== -1)
			|| (game.turn() === 'b' && piece.search(/^w/) !== -1)) {
		return false;
	}
};

var onDrop = function(source, target) {
	console.log("onDrop()");
	// see if the move is legal
	var move = game.move({
		from : source,
		to : target,
		promotion : 'q' // NOTE: always promote to a queen for example
	// simplicity
	});

	// illegal move
	if (move === null)
		return 'snapback';

	CHESS_MOVE_COUNTER++;
	CURRENT_CHESS_MOVE = move;

	updateStatus();
};

// update the board position after the piece snap
// for castling, en passant, pawn promotion
var onSnapEnd = function() {
	board.position(game.fen());
};

var updateStatus = function() {
	var status = '';

	var moveColor = 'White';
	if (game.turn() === 'b') {
		moveColor = 'Black';
	}

	// checkmate?
	if (game.in_checkmate() === true) {
		status = 'Game over, ' + moveColor + ' is in checkmate.';

		var winnerColor = "";
		var looserColor = "";
		if (moveColor == 'White') {
			winnerColor = 'black';
			winnerUsername = BLACK_COLOR_USERNAME;
			loserUsername = WHITE_COLOR_USERNAME;
		} else {
			winnerColor = 'white';
			winnerUsername = WHITE_COLOR_USERNAME;
			loserUsername = BLACK_COLOR_USERNAME;
		}

		// only winner send message, to prevent duplicates
		// browser client that create game.in_checkmate() is winner
		// by default
		if (WEBSOCKET_CLIENT_NAME == winnerUsername) {
			var fenString = fenFromYourMove.value;
			webSocket.send(JSON.stringify({
				type : "game-over",
				fen : fenString,
				winnerColor : winnerColor,
				winnerUsername : winnerUsername,
				checkMate : true,
				loserUsername : loserUsername,
				sendFrom : WEBSOCKET_CLIENT_NAME,
				sendTo : $('#quit-game-btn').data("gamePartner")
			}));
		}

		$('#move-for').html("<h1 class=\"text-success\">YOU WIN !</h1>");
		alert("check mate, you Win!");

		$('#startPosBtn').show();
		$('#game-status').data('isPlaying', false);
		$('#game-status').html('');
		$('#send-move-btn').data("opponentName", '');
		$('#quit-game-btn').data("gamePartner", '');
		$('#play-with-opponent-interface-actions').attr("hidden", true);
		OPPONENT_USERNAME = "";
		clearParticipantsListView();

	}

	// draw?
	else if (game.in_draw() === true) {
		status = 'Game over, drawn position';
	}

	// game still on
	else {
		status = moveColor + ' to move';

		// check?
		if (game.in_check() === true) {
			status += ', ' + moveColor + ' is in check';
		}
	}

	statusEl.html(status);
	fenEl.html(game.fen());
	$('#fenFromYourMove').val(game.fen());
	pgnEl.html(game.pgn());

	changeGameStatusBox();

};

var cfg = {
	pieceTheme : '/resources/images/chesspieces/wikipedia/{piece}.png',
	draggable : true,
	position : 'start',
	onDragStart : onDragStart,
	onDrop : onDrop,
	onSnapEnd : onSnapEnd
};
board = ChessBoard('board', cfg);

updateStatus();

function changeGameStatusBox() {
	console.log("$('#status') changed");

	var statusSpan = $('#status').text();
	if (statusSpan.match("Black to move")) {
		$('#play-with-user-stats-div').addClass('navbar-inverse white');
	} else {
		$('#play-with-user-stats-div').removeClass('navbar-inverse white');
	}

}