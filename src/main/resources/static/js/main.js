/**
 * main js
 */
$(document).ready(
		function() {

			// menu active btn
			$('#navButtons li:eq( 0 )').addClass('active')

			var url = window.location.href;
			$("#navButtons a").each(function() {

				if (url.match(this.href)) {
					$('#navButtons li:eq( 0 )').removeClass('active')
					$(this).closest("li").addClass("active");
				}
			});

			// datatable
			if (window.location.href == "http://" + window.location.host
					+ "/user/your-chessgames"
					|| window.location.href == "https://"
							+ window.location.host + "/user/your-chessgames") {
				$('#gamesTableForDataTableJS').dataTable();

			}

			if (window.location.href.match(window.location.host
					+ "/admin/users")) {
				$('#usersTableForDataTableJS').DataTable();

			}

		});