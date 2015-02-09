//Profile controller

//TODO: List
//TODO: 1. Create profile service and refactor
//TODO: 2. Add date picker to date of birth
//TODO: 3. Add tabs and directive for tabs for the existing client section
//TODO: 4. Add existing client directive and have its controller check for $scope.profile
//TODO: 5. Verify that when profile is prefilled or we select and existing client that we are using PUT and not POST for the client information
//TODO: 6. Make directive for financial assets and debts to be an accordion

app.controller('ProfileController', ["$scope", "$http", "profileService", "referralService", function ($scope, $http, profileService, referralService) {
	$scope.profile = {};
	$scope.profile.referral = {};
	$scope.profile.client = {};
	$scope.refStatus = [
		{
			"status": "OPEN"
		},
		{
			status: "CLOSED"
		},
		{
			status: "CANCELLED"
		}
	];

	/**
	 * Prepare the form when we prefill from a referral the client info
	 * @param refId
	 */
	$scope.prefillFromReferral = function (refId) {
		//Get Referral
		referralService.get(refId, function (referral) {
			$scope.profile.refId = refId;
			//Set profile's referral for tracking relationship between referral and profile
			$scope.profile.referral = referral;
			//Default status in select
			$scope.profile.referral.status = $scope.refStatus[0];

			//Set profile.client to the referral client
			$scope.profile.client = $scope.profile.referral.client;

			//Broadcast client information to child scopes
			$scope.$broadcast('profileClientSet');
		});
	};

	/**
	 * Handles profile view form submission
	 * @param profile
	 */
	$scope.addProfile = function (profile) {
		referralService.put($scope.profile.referral, function () {
			profileService.post(profile, function (profileId){
				window.location="/action/profileReview/" + profileId;
			});
		});
	};

	//Check the profile url for a prefill from a referral
	if(window.location.href.match(/\?refId=.*$/)){
		var param = window.location.href.match(/\?refId=.*$/)[0];
		param = param.slice(param.indexOf("=") + 1);
		$scope.prefillFromReferral(param);
	}
}]);