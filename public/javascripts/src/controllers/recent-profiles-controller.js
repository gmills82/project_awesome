//Fresh Referral Controller
app.controller('RecentProfilesController', ["$scope", "$http", function ($scope, $http){
	$scope.referrals = [];
	$http({"method": "GET", "url": "/json/recentProfiles/" + app.data.currentUserId}).success(function (data){
		$scope.referrals = data.data;
	});
}]);