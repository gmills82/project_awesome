//Fresh Referral Controller
app.controller('RecentProfilesController', ["$scope", "$http", function ($scope, $http){
	$scope.recentProfiles = [];
	this.init = function () {
		$http({"method": "GET", "url": "/json/recentProfiles/" + app.data.currentUserId}).success(function (data){
			//Pretty dates for profiles
			var profiles = data.data;
			for(var q = 0; q < profiles.length; q++) {
				date = new Date(profiles[q].createdDate);
				profiles[q].createdDatePretty = (date.getMonth() + 1) + "/" + date.getDate() + "/" + date.getFullYear();
			}
			$scope.recentProfiles = profiles;
		});
	}
}]);