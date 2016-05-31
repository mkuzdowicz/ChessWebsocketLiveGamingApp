/**
 * html objects on home page:
 * 
 * div#board, div#status, div#fen, div#pgn
 */

var board, game = new Chess();

statusEl = $('#status'), fenEl = $('#fen'), pgnEl = $('#pgn');

// do not pick up pieces if the game is over
// only pick up pieces for White
var onDragStart = function(source, piece, position, orientation) {
	if (game.in_checkmate() === true || game.in_draw() === true
			|| piece.search(/^b/) !== -1) {
		return false;
	}
	updateStatus();
};

var makeRandomMove = function() {
	var possibleMoves = game.moves();

	// game over
	if (possibleMoves.length === 0)
		return;

	var randomIndex = Math.floor(Math.random() * possibleMoves.length);
	game.move(possibleMoves[randomIndex]);
	board.position(game.fen());
	updateStatus();
};

var onDrop = function(source, target) {
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

	// make random legal move for black
	window.setTimeout(makeRandomMove, 250);
	updateStatus();
};

// update the board position after the piece snap
// for castling, en passant, pawn promotion
var onSnapEnd = function() {
	board.position(game.fen());
	updateStatus();
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

// ------------------------------------

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