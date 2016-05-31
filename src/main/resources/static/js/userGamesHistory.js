/**
 * 
 */

$(document).ready(function() {
	$('button.showEndPsoBtn').click(function() {

		var fen = $(this).data('fen');
		showChessGameEndingPos(fen);
	});
});

function showChessGameEndingPos(pos) {
	
	var cfg = {
			pieceTheme : '/resources/images/chesspieces/wikipedia/{piece}.png',
			position : pos
		};
	
	var boardInModal = ChessBoard('boardInModal', cfg);

	$('#chess-end-pos-modal').modal('show');
}

