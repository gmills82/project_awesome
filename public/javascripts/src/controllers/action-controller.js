//Actions controller
app.controller('ActionController', ["$scope", "$http", "$attrs", function ($scope, $http, $attrs){
	$scope.actions = [];
	$http({"method": "GET", "url": "/actions/" + app.data.currentUserId + "/" + $attrs.category}).success(function (data){
		$scope.actions = data.data;
	});
}]);