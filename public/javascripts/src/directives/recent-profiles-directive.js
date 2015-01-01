app.directive('recentProfiles', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'RecentProfilesController',
		link: function(scope, element, attrs, RecentProfilesController) {
			function initDataTable() {
				$(element).find('table').dataTable({
					columns:[null, null, null, null, null, null, null, {"orderable": false}],
					order: [[0, "asc"]],
					paging: false,
					searching: false,
					info: false
				});
			}

			RecentProfilesController.init();
			timer(initDataTable, 500);
		},
		templateUrl: "assets/javascripts/src/views/recent-profiles-view.html"
	};
}]);