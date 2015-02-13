/**
 * Profile service providing a POST method
 */
app.factory('clientService', ['$http', '$log', function($http, $log) {
	var service = {};

	var errorMsg = function (data, status, error) {
		$log.warn("Client service returned a status of " + status + "and the error:" + error);
	};

	service.get = function (clientId, callback) {
		$http.get("/json/client/" + clientId).success(function (data, status, headers) {
			if(typeof callback == 'function') {
				callback(data.data);
			}
		}).error(errorMsg);
	};

	service.put = function (client, callback) {
		$http.put("/json/client", client).success(function (data, status, headers) {
			if(typeof callback == 'function') {
				callback();
			}
		}).error(errorMsg);
	};

	service.post = function (client, callback) {
		$http.post("/json/client", client).success(function (data, status, headers) {
			if(typeof callback == 'function') {
				callback(data, status, headers);
			}
		}).error(errorMsg);
	};

	return service;
}]);