//Debt controller
app.controller('DebtController', ["$scope", "$http", function ($scope, $http) {
	$scope.current = {};
	$scope.debtTypes = [];
	$http({"method": "GET", "url": "/json/debtTypes"}).success(function (data){
		$scope.debtTypes = data.data;
	});
	$scope.adddebt = function (debt) {
		//Get current client
		$http.get("/json/client/" + $scope.$parent.profile.client.id).success(function (data, status, headers){
			//Add debt to client
			var client = data.data;
			client.debtList.push(debt);
			$scope.current = {};

			//Call client update which calls profile update
			$scope.$parent.$$childHead.addClient(client);
		}).error(function (xhr, status, err) {
			console.log("Debt unable to be added: " + err);
		});
	}
}]);