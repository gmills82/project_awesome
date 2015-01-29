//Actions controller
app.controller('ActionController', ["$scope", "$http", "$attrs", function ($scope, $http, $attrs){
	$scope.actions = [];
	$scope.getActions = function (category) {
		//Cache this request aggresively as navigation routes don't often change
		$http({"method": "GET", "url": "/actions/" + app.data.currentUserId + "/" + category, "cache": true}).success(function (data){
			$scope.actions = data.data;
		});
	}
}]);