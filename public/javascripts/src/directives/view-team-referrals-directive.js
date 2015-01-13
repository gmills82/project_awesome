app.directive('viewTeamReferrals', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'ViewTeamReferralsController',
		link: function(scope, element, attrs, ViewTeamReferralsController) {
			function initDataTable() {
				$(element).find('table').dataTable({
					columns:[null, null, null, null, null, null, null, null, null, {"orderable": false}],
					order: [[2, "asc"]]
				});

				$('#deleteModal').on('show.bs.modal', function (event) {
					var button = $(event.relatedTarget) // Button that triggered the modal
					var recipient = button.data('referral') // Extract info from data-*
					var modal = $(this);
					modal.find('.btn-primary').click(function () {
						scope.deleteReferral(recipient);
						modal.modal('hide');
					});
				});
			}

			ViewTeamReferralsController.init();
			timer(initDataTable, 500);
		},
		templateUrl: "/assets/javascripts/src/views/view-team-referrals.html"
	};
}]);