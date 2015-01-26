app.directive('referralsByCreator', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'ReferralsByCreatorController',
		link: function(scope, element, attrs, ReferralsByCreatorController) {
			function initDataTable() {
				$(element).find('table').dataTable({
					columns:[null, null, null, null, {"orderable": false}],
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

			ReferralsByCreatorController.init();
			timer(initDataTable, 1000);
		},
		templateUrl: "/assets/javascripts/src/views/referrals-by-creator.html"
	};
}]);