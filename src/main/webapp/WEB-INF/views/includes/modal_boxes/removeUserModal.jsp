<!-- REMOVE USER MODAL -->
<div class="modal fade" id="removeUser" aria-hidden="true">
	<div class="modal-dialog modal-sm">
		<div class="modal-content">
			<div class="modal-body">
				<p class="text-center">
					Are you sure to remove user <b><span id="userNameSpan"></span></b>
					?
				</p>
			</div>
			<div class="modal-body text-center">
				<form class="add-form" method="POST" action="/admin/users/remove">
					<input type="hidden" id="id" name="username">

					<p class="text-center">
						<button type="button" class="btn btn-default" data-dismiss="modal">No</button>
						<button type="submit" class="btn btn-danger">Yes, remove</button>
					</p>
				</form>
			</div>
		</div>
	</div>
</div>
<script>
	// remove template function ----------------------------------
	$('#removeUser').on('show.bs.modal', function(event) {

		var button = $(event.relatedTarget);
		var id = button.data('id');
		$('#userNameSpan').html(id);
		var modal = $(this);
		modal.find('input#id').val(id);

	});
</script>