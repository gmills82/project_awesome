//Client controller
app.controller('ClientController', ["$scope", "$http", function ($scope, $http) {
	$scope.client = {};
	$scope.client.goals = [];
	$scope.mode = "add";

	/**
	 * Returns formated date string of 18 years ago from today
	 * @returns {string}
	 */
	function getOfAgeDate() {
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
	}

	$scope.maxDate = getOfAgeDate();

	/**
	 * Pass updated client information to the profile controller upon client submission
	 * @param client
	 */
	$scope.prepareProfileScope = function (client) {
		$scope.profile.client = client;
		$scope.profile.clientId = client.id;
		$scope.profile.agentId = app.data.currentUserId;
		$scope.mode = "edit";
		//Show client information display tab pane
		angular.element('#clientTab a[href=#display]').tab('show');
	};

	/**
	 * Event listener that handles when the client is set in a parent scope
	 */
	$scope.$on("profileClientSet", function (client) {
		$scope.client = $scope.profile.client;
		$scope.mode = "edit";
	});

	/**
	 * Handles client form submission (profile view)
	 * @param client
	 */
	$scope.addClient = function (client) {
		if($scope.mode === "add") {
			$http.post("/json/client", client).success(function (data, status, headers) {
				//Lookup new resource with a GET
				$http.get(headers("Location")).success(function (data){
					$scope.prepareProfileScope(data.data);
					$scope.client = data.data;
				});
			}).error(failFunc);
		}else if ($scope.mode === 'edit') {
			$http.put("/json/client", client).success(function (data, status, headers) {
				//Lookup new resource with a GET
				$scope.prepareProfileScope(client);
				$scope.client = client;
			});
		}
	};

	//TODO: ?Is this really necessary or the best way?
	/**
	 * Sets active tab based on mode (add || edit)
	 */
	$scope.preparePanel = function () {
		//If add mode then land panel on the display tab
		if($scope.mode === "add") {
			angular.element('#clientTab a[href=#display]').show();
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
	}
}]);