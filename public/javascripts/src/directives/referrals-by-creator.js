app.directive('referralsByCreator', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'ReferralsByCreatorController',
		link: function(scope, element, attrs, ReferralsByCreatorController) {
			ReferralsByCreatorController.init();
		},
		templateUrl: "/assets/javascripts/src/views/referrals-by-creator.html"
	};
}]);