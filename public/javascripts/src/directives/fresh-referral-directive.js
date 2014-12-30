app.directive('freshReferral', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'FreshReferralController',
		link: function(scope, element, attrs, FreshReferralController) {
			function initDataTable() {
				$(element).find('table').dataTable({
					columns:[null, null, null, null, {"orderable": false}],
					order: [[2, "asc"]],
					paging: false,
					searching: false,
					info: false
				});
				console.log(element);
			}

			FreshReferralController.init();
			timer(initDataTable, 500);
		},
		templateUrl: "assets/javascripts/src/views/fresh-referral-view.html"
	};
}]);