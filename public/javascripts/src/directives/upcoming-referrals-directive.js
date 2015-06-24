app.directive('upcomingReferrals', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'UpcomingReferralsController',
		link: function(scope, element, attrs, UpcomingReferallsController) {
			function initDataTable() {

				$('#deleteModal').on('show.bs.modal', function (event) {
					var button = $(event.relatedTarget); // Button that triggered the modal
					var recipient = button.data('referral'); // Extract info from data-*
					var modal = $(this);
					modal.find('.btn-primary').click(function () {
						scope.deleteReferral(recipient);
						modal.modal('hide');
					});
				});
			}

			UpcomingReferallsController.init();
			timer(initDataTable, 1500);
		},
		templateUrl: "/assets/javascripts/src/views/upcoming-referrals-view.html"
	};
}]);