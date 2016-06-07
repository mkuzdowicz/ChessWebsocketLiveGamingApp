/**
 * protocol for websocket onmessage event
 */

var clientMsgProtocol = {

	proccessMessage : function(event) {

		if (event != null) {
			var message = JSON.parse(event.data);
			console.log("message");
			console.log(message);

			if (message.type == "chess-move") {
				console.log("chess-move");

				var fenStr = message.fen;
				if (fenStr != null && fenStr != "") {
					$('#fenFromPreviousMove').val(fenStr);

					board.position(fenStr);
					game = new Chess(fenStr);
					updateStatus();
				}

				SENDED_CHESS_MOVE_STATUS = message.moveStatus;
				CHESS_MOVE_COUNTER = 0;
				SEND_MOVE_CLICK_COUNTER = 0;

				showActualMoveStatus();

			} else if (message.type == "game-handshake-invitation") {
				console.log("game-handshake-invitation");

				showGameHandshakeModalBox(message.sendFrom);
				startTimeoutForHandshakeForInvitedUser();

				setChessColorGlobalVars(message);

				var yourChessColorsInfo = "you: <span class=\"text-info\"><b>"
						+ WEBSOCKET_CLIENT_NAME + "</b></span> : "
						+ message.sendToObj.chessColor + " ";

				$('#your-username').html(yourChessColorsInfo);

				var opponentChessColorsInfo = "opponent: <span class=\"text-info\"><b>"
						+ message.sendFrom
						+ "</b></span> : "
						+ message.sendFromObj.chessColor + " ";

				$('#opponent-username').html(opponentChessColorsInfo);

			} else if (message.type == "game-handshake-agreement") {
				console.log("game-handshake-agreement");

				CLICK_AGREEMENT_FLAG = true;
				SEND_MOVE_CLICK_COUNTER = 0;
				SENDED_CHESS_MOVE_STATUS = message.moveStatus;
				OPPONENT_USERNAME = message.sendFrom;

				$('#play-with-opponent-interface').attr("hidden", false);
				$('#play-with-opponent-interface-actions')
						.attr("hidden", false);
				$('#startPosBtn').hide();
				$('#fenFromPreviousMove').val(startFENPosition);
				$('#game-status').data('isPlaying', true);

				var youArePlayingWithInfo = "<div id=\"you-are-playing-with-info\" class=\"alert nice-red-bg-color text-center white\">"
						+ "<p>you are playing now with: <strong>"
						+ message.sendFrom + "</strong></p>" + "</div>";

				$('#game-status').html(youArePlayingWithInfo);

				var yourChessColorsInfo = "you: <span class=\"text-info\"><b>"
						+ WEBSOCKET_CLIENT_NAME + "</b></span> : "
						+ message.sendToObj.chessColor + " ";

				$('#your-username').html(yourChessColorsInfo);

				var opponentChessColorsInfo = "opponent: <span class=\"text-info\"><b>"
						+ message.sendFrom
						+ "</b></span> : "
						+ message.sendFromObj.chessColor + " ";

				$('#opponent-username').html(opponentChessColorsInfo);
				$('#send-move-btn').data("opponentName", message.sendFrom);
				$('#quit-game-btn').data("gamePartner", message.sendFrom);

				var agreementModalInfo = "game agreement from user: "
						+ "<span class=\"text-primary\"><b>" + message.sendFrom
						+ "</b></span>";

				$('#game-handshake-response-modal-title').html(
						agreementModalInfo);

				$('#game-handshake-response-modal').modal('show');
				startNewGame();
				showActualMoveStatus();
				setChessColorGlobalVars(message);

			} else if (message.type == "game-handshake-refuse") {
				console.log("game-handshake-refuse");

				CLICK_REFUSED_FLAG = true;
				SENDED_CHESS_MOVE_STATUS = "";

				var refuseModalInfo = "game refused from user: "
						+ "<span class=\"text-primary\"><b>" + message.sendFrom
						+ "</b></span>";

				$('#game-handshake-response-modal-title').html(refuseModalInfo);
				$('#game-handshake-response-modal').modal('show');
				$('#game-status').html('');

			} else if (message.type == "quit-game"
					|| message.type == "goodbye-msg") {

				OPPONENT_USERNAME = "";

				if (message.type == "quit-game") {
					alert("user quit game");
					CHESS_MOVE_COUNTER = 0;
				}

				$('#startPosBtn').show();
				$('#game-status').data('isPlaying', false);
				$('#play-with-opponent-interface').attr("hidden", true);
				$('#game-status').html('');
				$('#opponent-username').html('');
				$('#send-move-btn').data("opponentName", '');
				$('#quit-game-btn').data("gamePartner", '');

				clearParticipantsListView();

			} else if (message.type == "try-later") {

				alert("user is playing now with someone else, \n or is during handshake with someone else,\n try later.");

			} else if (message.type == "game-over") {

				SENDED_CHESS_MOVE_STATUS = message.moveStatus;
				OPPONENT_USERNAME = "";

				var fenStr = message.fen;
				if (fenStr != null && fenStr != "") {

					$('#fenFromPreviousMove').val(fenStr);
					board.position(fenStr);
					game = new Chess(fenStr);
					updateStatus();
				}

				showActualMoveStatus();

				if (message.checkMate == true) {
					$('#move-for').html(
							"<h1 class=\"text-danger\">YOU LOOSE !</h1>");
					alert("check mate, you loose!");

					$('#startPosBtn').show();
					$('#game-status').data('isPlaying', false);
					$('#game-status').html('');
					$('#send-move-btn').data("opponentName", '');
					$('#quit-game-btn').data("gamePartner", '');
					$('#play-with-opponent-interface-actions').attr("hidden",
							true);

				}

				clearParticipantsListView();

			} else {
				showParticipants(event.data);
				clearParticipantsListView();
			}
		}

	}

}