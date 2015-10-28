app.directive('freshReferrals', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'FreshReferralController',
		link: function(scope, element, attrs, FreshReferralController) {
			FreshReferralController.init();
		},
		templateUrl: "assets/javascripts/src/views/fresh-referral-view.html"
	};
}]);