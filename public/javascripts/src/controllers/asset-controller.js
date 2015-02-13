//Assets controller
app.controller('AssetController', ["$scope", "$http", "clientService", function ($scope, $http, clientService) {
	$scope.current = {};
	$scope.assetTypes = [];
	$http({"method": "GET", "url": "/json/assetTypes"}).success(function (data){
		$scope.assetTypes = data.data;
	});
	$scope.addAsset = function (asset) {
		//Get current client
		clientService.get($scope.profile.client.id, function (client) {

			//Add asset to client
			if(typeof(client.assetList) == 'undefined') {
				client.assetList = [];
			}
			client.assetList.push(asset);

			//Clear current asset form
			$scope.current = {};

			//Update profile client scope
			$scope.profile.client.assetList = client.assetList;
			//Update in database
			clientService.put(client);
		});
	}
}]);