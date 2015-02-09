//Client controller
app.controller('ClientController', ["$scope", "$http", "referralService", function ($scope, $http, referralService) {
	$scope.client = {};
	$scope.client.goals = [];

	/**
	 * Returns formated date string of 18 years ago from today
	 * @returns {string}
	 */
	this.getOfAgeDate = function() {
		var date = new Date();
		date.setFullYear(date.getFullYear() - 18);
		var dateString = date.getFullYear() + "-";
		var month = date.getMonth() + 1;
		if (month.toString().length < 2) {
			month = "0" + month;
		}
		var day = date.getDate() + 1;
		if (day.toString().length < 2) {
			day = "0" + day;
		}
		dateString += month + "-" + day;

		return dateString;
	};

	$scope.maxDate = this.getOfAgeDate();

	/**
	 * Handles client profile view form submission
	 * @param client
	 */
	$scope.addClientFormSubmission = function (profile) {
		var client = profile.client;
		var referral = profile.referral;

		//Set profile agentId on form submission
		$scope.profile.agentId = app.data.currentUserId;

		//If new client the post
		if(profile.client.mode === "add") {
			$http.post("/json/client", client).success(function (data, status, headers) {
				//Lookup new resource with a GET
				$http.get(headers("Location")).success(function (data){
					//Used to allow profile form to submit
					$scope.profile.clientId = data.data.id;
				});
			});
		}else if (profile.client.mode === 'edit') {
			//Used to allow profile form to submit
			$scope.profile.clientId = client.id;
			$http.put("/json/client", client).success(function (data, status, headers) {
				referralService.put(referral);
			});
		}
	};

	/**
	 * Used to pass clients to the client search view
	 * @param query
	 */
	this.queryClients = function (query) {
		$http.get('/json/client/query/' + query).success(function (data, status, headers) {
			$scope.clients = data.data;
		});
	};

}]);