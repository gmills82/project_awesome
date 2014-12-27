//Fresh Referral Controller
app.controller('FreshReferralController', ["$scope", "$http", function ($scope, $http){
	$scope.referrals = [];
	$http({"method": "GET", "url": "/json/referrals/" + app.data.currentUserId}).success(function (data){
		$scope.referrals = data.data;
	});
}]);