//Profile controller
app.controller('ProfileController', ["$scope", "$http", function ($scope, $http) {
	$scope.profile = {};
	$scope.profile.client = {};
	$scope.prefillFromReferral = function (refId) {
		//Get Referral
		$http.get("/json/referral/" + refId).success(function (data, status, headers) {
			var referral = data.data;
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
	}

	if(window.location.href.match(/\?refId=.*$/)){
		var param = window.location.href.match(/\?refId=.*$/)[0];
		param = param.slice(param.indexOf("=") + 1);
		$scope.prefillFromReferral(param);
	}

	$scope.addProfile = function (profile) {
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
	};
}]);