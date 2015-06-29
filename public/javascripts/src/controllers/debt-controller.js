//Debt controller
app.controller('DebtController', ["$scope", "$http", "clientService", function ($scope, $http, clientService) {
	$scope.current = {};
	$scope.debtTypes = [];
	$http({"method": "GET", "url": "/json/debtTypes"}).success(function (data){
		$scope.debtTypes = data.data;
	});
	$scope.adddebt = function (debt) {
		//Get current client
		clientService.get($scope.referral.client.id, function (client){
			//Add debt to client
			if(typeof(client) == 'undefined') {
				client.debtList = [];
			}
			client.debtList.push(debt);

			//Clear current debt form
			$scope.current = {};

			//Update referral scope to show changes
			$scope.referral.client.debtList = client.debtList;

			clientService.put(client);
		})
	}
}]);