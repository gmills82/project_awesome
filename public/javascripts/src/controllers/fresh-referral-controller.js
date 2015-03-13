//Fresh Referral Controller
app.controller('FreshReferralController', ["$scope", "$http", "ngTableParams", "$filter", function ($scope, $http, ngTableParams, $filter){
	$scope.referrals = [];
	this.init = function () {
		$scope.columns = [
			{title: 'Type', field: 'type'},
			{title: 'Client Name', field: 'name'},
			{title: 'Next Steps Date', field: 'nextStepDate'},
			{title: 'Phone Number', field: 'phone'},
			{title: 'Status', field: 'status'},
			{title: 'Actions', field: 'actions'},
			{title: 'Created By', field: 'creator'},
			{title: 'Last Edited Date', field: 'lastEditedDate'}
		];
		$scope.freshReferralsTable = new ngTableParams({
			page: 1,            // show first page
			count: 10,
			sort: {
				lastEditedDate: "dsc"
			}
		}, {
			getData: function($defer, params) {
				$http({"method": "GET", "url": "/json/referrals/" + app.data.currentUserId}).success(function (data){
					var orderedData = params.sorting() ?
						$filter('orderBy')(data.data, params.orderBy()) :
						data;
					$scope.total = orderedData.length;
					params.total(orderedData.length);

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