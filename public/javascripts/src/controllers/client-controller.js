//Client controller
app.controller('ClientController', ["$scope", "$http", "referralService", "clientService", function ($scope, $http, referralService, clientService) {
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

		$scope.profile.client.status = "Verifying";
		$scope.profile.client.statusClass = "info";

		//Used to allow profile form to submit
		$scope.profile.clientId = client.id;

		//Update client information
		clientService.put(client, function () {
			//Update referral information
			referralService.put(referral);
			//Update view to show saved success
			$scope.profile.client.status = "Verified";
			$scope.profile.client.statusClass = "success";
		});
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