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
				referral.user_id = referral.agentId;

				//Save the referral
				$http.post("/json/referral", referral).success(function (data, status, headers){
					if(typeof(callback) == "function") {
						callback(referral);
					}
				}).error(errorMessaging);
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

    /**
     Deletes a referral by the provided ID

     @param     {Number}        id              Referral ID
     @param     {Function}      [callback]      Callback method
     */
    service.deleteById = function (id, callback) {
        $http({"method": "DELETE", "url": "/json/referral/" + id}).success(function (data){
            if (typeof callback === "function") {
                callback(data);
            }
        });
    };

    /**
     Returns the possible referral types. This list is small right now, so making it static isn't a big deal. However,
     if it starts becoming difficult to maintain, we can move the list to the server so it can be managed in a single place.

     @param     {Function}      [callback]      Callback method
     */
    service.getReferralTypes = function (callback) {
        if (typeof callback === 'function') {
            callback(null, [
                {
                    'id': 'Callout',
                    'title': 'Callout'
                },
                {
                    'id': 'Appt',
                    'title': 'Appointment'
                },
                {
                    'id': 'Quote',
                    'title': 'Quote'
                },
                {
                    'id': 'Profiles Review',
                    'title': 'Profiles Review'
                },
                {
                    'id': 'Declined',
                    'title': 'Declined'
                },
                {
                    'id': 'Follow Up',
                    'title': 'Follow Up'
                },
                {
                    'id': 'Seminar',
                    'title': 'Seminar'
                }
            ]);
        }
    };

	return service;
}]);