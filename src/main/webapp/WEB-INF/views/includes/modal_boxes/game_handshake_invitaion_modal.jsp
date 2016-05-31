<!-- USER GAME INVITAION MODAL -->
<div class="modal fade" id="game-handshake-modal" aria-hidden="true"
	data-keyboard="false" data-backdrop="static">
	<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<div class="modal-header">
				<p class="text-center">
				<h4 class="modal-title text-center" id="game-handshake-modal-title"></h4>
			</div>
			<div class="modal-body text-center">
				<input id="game-handshake-msgTo" type="hidden" />

				<div class="text-center">
					<div id="game-handshake-timer-icon" class="text-center glyphicon glyphicon-time"></div>
					<div id="game-handshake-timer"></div>
					<br />
					<button type="button" onclick="agreementToPlay()"
						class="btn btn-danger">Yes</button>
					<button type="button" onclick="refusedToPlay()"
						class="btn btn-default">No</button>
				</div>

			</div>
		</div>
	</div>
</div>