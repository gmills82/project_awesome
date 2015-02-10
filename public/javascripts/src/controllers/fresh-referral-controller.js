//Fresh Referral Controller
app.controller('FreshReferralController', ["$scope", "$http", function ($scope, $http){
	$scope.referrals = [];
	this.init = function () {
		$http({"method": "GET", "url": "/json/referrals/" + app.data.currentUserId}).success(function (data){
			$scope.referrals = data.data;
		});
	};
	$scope.deleteReferral = function (refId) {
		$http({"method": "DELETE", "url": "/json/referral/" + refId}).success(function (data){
			for(var y = 0; y < $scope.referrals.length; y++) {
				if($scope.referrals[y].id === refId) {
					$scope.referrals.splice(y, 1);
				}
			}
		});
	}
}]);