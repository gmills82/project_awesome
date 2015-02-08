//Profile controller

//TODO: List
//TODO: 1. Create profile service and refactor
//TODO: 2. Add date picker to date of birth
//TODO: 3. Add tabs and directive for tabs for the existing client section
//TODO: 4. Add existing client directive and have its controller check for $scope.profile
//TODO: 5. Verify that when profile is prefilled or we select and existing client that we are using PUT and not POST for the client information
//TODO: 6. Make directive for financial assets and debts to be an accordion

app.controller('ProfileController', ["$scope", "$http", function ($scope, $http) {
	$scope.profile = {};
	$scope.profile.referral = {};
	$scope.profile.client = {};
	$scope.prefillFromReferral = function (refId) {
		//Get Referral
		$http.get("/json/referral/" + refId).success(function (data, status, headers) {
			var referral = data.data;
			$scope.profile.refId = refId;

			//Set profile's referral for tracking relationship between referral and profile
			$scope.profile.referral = referral;
			//Default status in select
			$scope.profile.referral.status = $scope.refStatus[0];

			//Get Client
			$http.get("/json/client/" + referral.clientId).success(function (data, status, headers) {
				var client = data.data;
				//Prefill notes
				client.refNotes = referral.refNotes;

				//Prefill client info
				$scope.profile.client = client;
				$scope.$$childHead.client = client;
				$scope.$$childHead.mode = "edit";


			}).error(function (error) {
				console.log("Client could not be retrieved from the referral with id " + refId);
			})
		}).error(function (error) {
			console.log("Referral with id " + refId + " could not be retrieved.");
		});
	};

	//Match referral Id param
	if(window.location.href.match(/\?refId=.*$/)){
		var param = window.location.href.match(/\?refId=.*$/)[0];
		param = param.slice(param.indexOf("=") + 1);
		$scope.prefillFromReferral(param);
	}

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

	$scope.addProfile = function (profile) {
		var referral = $scope.profile.referral;
		$http.put("/json/referral", referral).success(function (data, status, headers) {
			console.log("Referral with id " + referral.id + " updated to no longer be fresh");

			$http.post("/json/profile", profile).success(function (data, status, headers){
				var profileId = headers("Location").match(/\/\d*$/);
				if(profileId.length) {
					profileId = profileId[0];
					profileId = profileId.slice(1);
					window.location="/action/profileReview/" + profileId;
				}else {
					console.log(profileId, "Profile id was not pulled from the Location header");
				}

			}).error(function(jqxhr, status, error){
				console.log("Could not save profile. Server responsded with " + error);
			});
		}).error(function (error) {
			console.log("Referral could not be updated with id " + referral.id);
		});
	};
}]);