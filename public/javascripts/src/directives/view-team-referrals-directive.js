app.directive('viewTeamReferrals', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'ViewTeamReferralsController',
		link: function(scope, element, attrs, ViewTeamReferralsController) {
			ViewTeamReferralsController.init();
		},
		templateUrl: "/assets/javascripts/src/views/view-team-referrals.html"
	};
}]);