app.directive('upcomingReferrals', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'UpcomingReferralsController',
		link: function(scope, element, attrs, UpcomingReferralsController) {
			UpcomingReferralsController.init();
		},
		templateUrl: "/assets/javascripts/src/views/upcoming-referrals-view.html"
	};
}]);