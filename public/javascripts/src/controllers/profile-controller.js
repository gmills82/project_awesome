//Profile controller
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

	$scope.profile.client.status = "Not Verified";
	$scope.profile.client.statusClass = "warning";

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

			$scope.profile.client.status = "Not Verified";
			$scope.profile.client.statusClass = "warning";
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
		//Remove hash from tabs if present
		if(param.indexOf('#') != -1) {
			param = param.split("#")[0];
		}
		param = param.slice(param.indexOf("=") + 1);
		$scope.prefillFromReferral(param);
	}
}]);