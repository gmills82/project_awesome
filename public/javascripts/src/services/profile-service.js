/**
 * Profile service providing a POST method
 */
app.factory('profileService', ['$http', '$log', function($http, $log) {
	var service = {};

	var errorMsg = function (data, status, error) {
		$log.warn("Profile service returned a status of " + status + "and the error:" + error);
	};

	service.post = function (profile, callback) {
		$http.post("/json/profile", profile).success(function (data, status, headers){
			var profileId = headers("Location").match(/\/\d*$/);
			if(profileId.length) {
				profileId = profileId[0];
				profileId = profileId.slice(1);

			}
			if(typeof(callback) == "function") {
				callback(profileId);
			}
		}).error(errorMsg);
	};

	return service;
}]);