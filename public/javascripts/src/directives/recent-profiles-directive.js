app.directive('recentProfiles', [function() {
	return {
		restrict: 'A',
		controller: 'RecentProfilesController',
		link: function(scope, element, attrs, RecentProfilesController) {
			RecentProfilesController.init();
		},
		templateUrl: "assets/javascripts/src/views/recent-profiles-view.html"
	};
}]);