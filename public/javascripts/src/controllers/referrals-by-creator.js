//Referrals By Creator Controller
app.controller('ReferralsByCreatorController', ["$scope", "$http", function ($scope, $http){
	$scope.referrals = [];
	this.init = function () {
		$http({"method": "GET", "url": "/json/referrals/creator/" + app.data.currentUserId}).success(function (data){
			$scope.referrals = data.data;
		});
	}
	$scope.deleteReferral = function (refId) {
		$http({"method": "DELETE", "url": "/json/referral/" + refId}).success(function (data){
			$scope.referrals.each(function(index, ele, all) {
				if(ele.id === refId) {
					$scope.referrals[index] = null;
				}
			});
		});
	}
}]);