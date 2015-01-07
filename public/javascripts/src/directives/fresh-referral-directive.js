app.directive('freshReferrals', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'FreshReferralController',
		link: function(scope, element, attrs, FreshReferralController) {
			function initDataTable() {
				$(element).find('table').dataTable({
					columns:[null, null, null, null, {"orderable": false}, {"orderable": false}],
					order: [[2, "asc"]],
					paging: false,
					searching: false,
					info: false
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

			FreshReferralController.init();
			timer(initDataTable, 500);
		},
		templateUrl: "assets/javascripts/src/views/fresh-referral-view.html"
	};
}]);