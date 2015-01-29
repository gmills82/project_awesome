//Actions controller
app.controller('ActionController', ["$scope", "$http", "$attrs", function ($scope, $http, $attrs){
	$scope.actions = [];
	$scope.getActions = function (category) {
		$http({"method": "GET", "url": "/actions/" + app.data.currentUserId + "/" + category}).success(function (data){
			$scope.actions = data.data;
		});
	}
}]);