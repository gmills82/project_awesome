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
			}

			ReferralsByCreatorController.init();
			timer(initDataTable, 500);
		},
		templateUrl: "/assets/javascripts/src/views/referrals-by-creator.html"
	};
}]);