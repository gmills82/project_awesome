//Client controller
app.controller('ClientController', ["$scope", "$http", function ($scope, $http) {
	$scope.client = {};
	$scope.client.goals = [];
	$scope.mode = "add";
	$scope.prepareProfileScope = function (client) {
		$scope.$parent.profile.client = client;
		$scope.$parent.profile.clientId = client.id;
		$scope.$parent.profile.agentId = app.data.currentUserId;
		$scope.mode = "edit";
		//Show client information display tab pane
		$('#clientTab a[href=#display]').tab('show');
	};
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

	$scope.preparePanel = function () {
		//If add mode then land panel on the display tab
		if($scope.mode === "add") {
			$('#clientTab a[href=#display]').show();
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