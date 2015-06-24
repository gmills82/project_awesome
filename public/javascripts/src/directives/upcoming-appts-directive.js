app.directive('upcomingAppts', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'UpcomingApptsController',
		link: function(scope, element, attrs, UpcomingApptsController) {
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

			UpcomingApptsController.init();
			timer(initDataTable, 1500);
		},
		templateUrl: "/assets/javascripts/src/views/upcoming-appts-view.html"
	};
}]);