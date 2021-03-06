/**
 * Service to GET, POST, and UPDATE referrals
 */
app.factory('referralService', ['$http', '$log', 'clientService', function($http, $log, clientService) {
	var service = {};

	var errorMessaging = function (error, status) {
		$log.warn("referralService returned a status of " + status + "and an error: " + error);
	};

    var
        _referralTypes = [];

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

        // Referral notes are now handled by a separate service. Including them in the referral causes issues when
        // mapping to a model on the server, so we'll kill these before submission.
        if (referral) {
            if (referral.notes) {
                delete referral.notes;
            }
            if (referral.referralNotes) {
                delete referral.referralNotes;
            }
            if (referral.refNotes) {
                delete referral.refNotes;
            }
        }

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
     Adds a new note to the referral

     @param     {String}        note            Note to add
     @param     {Number}        referralId      Referral ID
     @param     {Function}      [callback]      Callback method
     */
    service.addNoteToReferral = function (note, referralId, callback) {
        $http({
            "method": "POST",
            "url": "/json/referral/note",
            data: {
                note: note,
                referralId: referralId,
                userModelId: app.data.currentUserId
            }
        }).success(function (data) {
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
            if (!_referralTypes || _referralTypes.length === 0) {
                _referralTypes = [
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
                ];
            }
            callback(null, _referralTypes);
        }
    };

    /**
     Returns the referral type for the provided ID

     @param     {String}        id              Referral type ID
     @param     {Function}      [callback]      Callback method
     */
    service.getReferralTypeById = function (id, callback) {
        if (typeof id !== "string") {
            return callback("ID must be provided as a string");
        }
        service.getReferralTypes(function (error, data) {
            if (error || !data) {
                return callback("Error getting referral types from the service");
            }
            for (var i = 0, length = data.length; i < length; i++) {
                if (data[i].id === id) {
                    return callback(null, data[i]);
                }
            }
            return callback(null, null);
        });
    };

    /**
     Returns the referrals for the team of the provided user ID

     @param     {Number}        userId          User ID
     @param     {Function}      [callback]      Callback method
     @todo      This currently only supports agents. Update to use any role if this gets called more often.
     */
    service.getTeamReferrals = function (userId, callback) {
        $http.get('/agent/' + userId + '/team/referrals').success(function (data) {
            if (typeof callback === "function") {
                callback(data);
            }
        });
    };

    /**
     Returns the processing referrals for the provided user ID

     @param     {Number}        userId          User ID
     @returns   {HttpPromise}
     */
    service.getProcessingReferrals = function (userId) {
        return $http.get('/json/referrals/processing/' + userId);
    };

    service.getByClientId = function (clientId) {
        return $http.get('/')
    }

	return service;
}]);