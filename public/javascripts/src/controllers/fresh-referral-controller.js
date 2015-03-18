//Fresh Referral Controller
app.controller('FreshReferralController', ["$scope", "$http", "ngTableParams", "$filter", function ($scope, $http, ngTableParams, $filter){
	$scope.referrals = [];
	this.init = function () {

		$scope.freshReferralsTable = new ngTableParams({
			page: 1,            // show first page
			count: 10,
			sorting: {
				lastEditedDate: 'desc'
			}
		}, {
			getData: function($defer, params) {
				$http({"method": "GET", "url": "/json/referrals/" + app.data.currentUserId}).success(function (data){
					//Filter
					var filteredData = params.filter() ?
						$filter('filter')(data.data, params.filter()) :
						data.data;
					//Then sort
					var orderedData = params.sorting() ?
						$filter('orderBy')(filteredData, params.orderBy()) :
						filteredData;

					//Pass out total to larger scope
					$scope.total = orderedData.length;
					params.total(orderedData.length);

					//Resolve data gathering
					$defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
				});
			}
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