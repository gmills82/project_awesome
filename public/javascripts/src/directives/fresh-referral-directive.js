app.directive('freshReferrals', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'FreshReferralController',
		link: function(scope, element, attrs, FreshReferralController) {
			function initDataTable() {
				$(element).find('table').dataTable({
					columns:[null, null, null, null, {"orderable": false}, {"orderable": false}, null, null],
					order: [[7, "desc"]],
					"iDisplayLength": 5,
					"aLengthMenu": [5,10,20,50]
				});

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

			FreshReferralController.init();
			timer(initDataTable, 1000);
		},
		templateUrl: "assets/javascripts/src/views/fresh-referral-view.html"
	};
}]);