/**
 * Service to GET, POST, and UPDATE referrals
 */
app.factory('referralService', ['$http', '$log', 'clientService', function($http, $log, clientService) {
	var service = {};

	var errorMessaging = function (error, status) {
		$log.warn("referralService returned a status of " + status + "and an error: " + error);
	};

	service.get = function (refId, callback) {
		$http.get("/json/referral/" + refId).success(function ( data, status, headers) {
			var referral = data.data;

			//Gather client information for referral as well
			clientService.get(referral.clientId, function(client) {
				referral.client = client;
				if(typeof(callback) == "function") {
					callback(referral);
				}

			});
		}).error(errorMessaging);
	};

	service.post = function (referral, callback) {
		//Check flag for existing client and update or add respectively
		if(referral.updateExistingClientFlag) {
			clientService.put(referral.client, function() {

				//Prepare client information on referral data
				referral.clientId = referral.client.id;
				referral.clientName = referral.client.name;
				referral.creatorId = app.data.currentUserId;

				//Add referral to user
				$http.get("/json/user/" + referral.agentId).success(function (data, status, headers) {
					var user = data.data;

					//Add referral to the user
					user.referrals.push(referral);

					//Update user/agent with this referral
					//Referral is actually saved HERE <-------------
					$http.put("/json/user", user).success(function (data, status, headers) {
						if(typeof(callback) == "function") {
							callback(referral);
						}
					})
				})
			})
		}else {
			clientService.post(referral.client, function (data, status, headers) {
				//Prepare client information on referral data
				//Parse headers for id
				var locationHeader = headers("LOCATION");
				referral.clientId = locationHeader.match(/\d*$/)[0];
				referral.clientName = referral.client.name;
				referral.creatorId = app.data.currentUserId;
				referral.user_id = referral.agentId;

				//Save the referral
				$http.post("/json/referral", referral).success(function (data, status, headers){
					if(typeof(callback) == "function") {
						callback(referral);
					}
				}).error(errorMessaging);
			});
		}
	};

	service.put = function (referral, callback) {
		$http.put("/json/referral", referral).success(function ( data, status, headers) {
			clientService.put(referral.client, function () {
				if(typeof(callback) == "function") {
					callback();
				}
			});
		}).error(errorMessaging);
	};

	return service;
}]);