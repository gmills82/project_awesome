//Assets controller
app.controller('AssetController', ["$scope", "$http", function ($scope, $http) {
	$scope.current = {};
	$scope.assetTypes = [];
	$http({"method": "GET", "url": "/json/assetTypes"}).success(function (data){
		$scope.assetTypes = data.data;
	});
	$scope.addAsset = function (asset) {
		//Get current client
		$http.get("/json/client/" + $scope.$parent.profile.client.id).success(function (data, status, headers){
			//Add asset to client
			var client = data.data;
			client.assetList.push(asset);
			$scope.current = {};

			//Call client update which calls profile update
			$scope.$parent.$$childHead.addClient(client);
		}).error(function (xhr, status, err) {
			console.log("Asset unable to be added: " + err);
		});
	}
}]);