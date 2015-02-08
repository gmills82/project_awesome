/**
 * Service to GET, POST, and UPDATE referrals
 */
app.factory('referralService', ['$http', '$log', function($http, $log) {
	var service = {};

	var errorMessaging = function (error, status) {
		$log.warn("referralService returned a status of " + status + "and an error: " + error);
	};

	service.get = function (refId, callback) {
		$http.get("/json/referral/" + refId).success(function ( data, status, headers) {
			var referral = data.data;

			//Gather client information for referral as well
			$http.get("/json/client/" + $scope.referral.clientId).success(function ( data, status, headers) {
				referral.client = data.data;
				callback(referral);

			}).error(errorMessaging);
		}).error(errorMessaging);
	};

	service.post = function (referral, callback) {
		function completeReferralPost() {
			//Get up to the minute client information and ID
			$http.get(headers("LOCATION")).success(function (data) {

				//Prepare client information on referral data
				referral.clientId = data.data.id;
				referral.clientName = data.data.name;
				referral.creatorId = app.data.currentUserId;

				//Add referral to user
				$http.get("/json/user/" + referral.agentId).success(function (data, status, headers) {
					var user = data.data;

					//Add referral to the user
					user.referrals.push(referral);

					//Update user/agent with this referral
					//Referral is actually saved HERE <-------------
					$http.put("/json/user", user).success(function (data, status, headers) {
						callback(referral);
					}).error(errorMessaging);
				}).error(errorMessaging);
			}).error(errorMessaging);
		}

		//Check flag for existing client and update or add respectively
		if(referral.updateExistingClientFlag) {
			$http.put("/json/client", referral.client).success(function(data, status, headers) {
				completeReferralPost();
			})
		}else {
			$http.post("/json/client", referral.client).success(function (data, status, headers) {
				completeReferralPost();
			}).error(errorMessaging);
		}
	};

	service.put = function (referral, callback) {
		$http.put("/json/referral", referral).success(function ( data, status, headers) {
			$http.put("/json/client", referral.client).success(function ( data, status, headers) {
				callback();
			}).error(errorMessaging);
		}).error(errorMessaging);
	};

	return service;
}]);