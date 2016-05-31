/**
 * chess random computer vs random computer
 * 
 * html objects on home page:
 * 
 * div#board, div#status, div#fen
 */

var board, game = new Chess();

var cfg = {
	pieceTheme : '/resources/images/chesspieces/wikipedia/{piece}.png',
	position : 'start'
};

board = ChessBoard('board', cfg);

statusEl = $('#status'), fenEl = $('#fen'), pgnEl = $('#pgn');

var makeRandomMove = function() {
	var possibleMoves = game.moves();

	// exit if the game is over
	if (game.game_over() === true || game.in_draw() === true
			|| possibleMoves.length === 0)
		return;

	var randomIndex = Math.floor(Math.random() * possibleMoves.length);
	game.move(possibleMoves[randomIndex]);
	board.position(game.fen());

	window.setTimeout(makeRandomMove, 2000);
	updateStatus();
};

window.setTimeout(makeRandomMove, 2000);

var updateStatus = function() {
	var status = '';

	var moveColor = 'White';
	if (game.turn() === 'b') {
		moveColor = 'Black';
	}

	// checkmate?
	if (game.in_checkmate() === true) {
		status = 'Game over, ' + moveColor + ' is in checkmate.';
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
	pgnEl.html(game.pgn());
};

updateStatus();
