//Client controller
app.controller('ClientController', ["$scope", "$http", function ($scope, $http) {
	$scope.client = {};
	$scope.client.goals = [];
	$scope.mode = "add";

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

	$scope.addClient = function (client) {
		var failFunc = function(jqxhr, status, error) {
			console.log("Client was unable to be updated. Server responded with " + status + " error: " + error);
		}

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
			}).error(failFunc);
		}
	};

	//TODO: ?Is this really necessary or the best way?
	$scope.preparePanel = function () {
		//If add mode then land panel on the display tab
		if($scope.mode === "add") {
			angular.element('#clientTab a[href=#display]').show();
		}
	};

	$scope.prepareAddClientPanel = function () {
		//Button functionality
		$('.btn').click(function () {
			$(this).siblings().removeClass('active');
			$(this).addClass('active');
		});

		//Check active buttons on tab show
		$('a[href=#client]').on("shown", function () {
			$('input[type="radio"]').each(function (index, ele) {
				if($(ele).parents("label").hasClass('active')) {
					$(ele).click();
					$scope.$apply()
				}
			});
		});
	};

	this.queryClients = function (query) {
		$http.get('/json/client/query/' + query).success(function (data, status, headers) {
			$scope.clients = data.data;
		}).error(function () {
			console.log("Failed to retrieve clients");
		});
	}
}]);