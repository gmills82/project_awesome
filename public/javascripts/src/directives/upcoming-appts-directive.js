app.directive('upcomingAppts', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'UpcomingApptsController',
		link: function(scope, element, attrs, UpcomingApptsController) {
			UpcomingApptsController.init();
		},
		templateUrl: "/assets/javascripts/src/views/upcoming-appts-view.html"
	};
}]);