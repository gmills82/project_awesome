//Fresh Referral Controller
app.controller('UpcomingReferralsController', ["$scope", "$http", "ngTableParams", "$filter", function ($scope, $http, ngTableParams, $filter){
	$scope.referrals = [];
	$scope.refTypes = [{'title': 'No Filter', 'id': ''}];
	$scope.arr=[];

	//Helper function
	var inArray = Array.prototype.indexOf ?
		function (val, arr) {
			return arr.indexOf(val)
		} :
		function (val, arr) {
			var i = arr.length;
			while (i--) {
				if (arr[i] === val) return i;
			}
			return -1;
		};

	this.init = function () {
		$scope.upcomingReferralsTable = new ngTableParams({
			page: 1,            // show first page
			count: 10,
			sorting: {
				nextStepDate: 'asc'
			}
		}, {
			getData: function($defer, params) {
				$http({"method": "GET", "url": "/json/referrals/upcoming/" + app.data.currentUserId}).success(function (data){
					//Filter
					var filteredData = params.filter() ?
						$filter('filter')(data.data, params.filter()) :
						data.data;
					//Then sort
					var orderedData = params.sorting() ?
						$filter('orderBy')(filteredData, params.orderBy()) :
						filteredData;

					//Pass out total to larger scope
					$scope.refTotal = orderedData.length;
					params.total(orderedData.length);

					//Create scope for RefType filter
					angular.forEach(orderedData, function(item){
						if (inArray(item.refType, $scope.arr) === -1) {
							$scope.arr.push(item.refType);
							$scope.refTypes.push({
								'id': item.refType,
								'title': item.refType
							});
						}
					});

					//Resolve data gathering
					$defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
				});
			}
		});

	};

	$scope.deleteReferral = function (refId) {
		$http({"method": "DELETE", "url": "/json/referral/" + refId}).success(function (data){
			for(var y = 0; y < $scope.upcomingReferralsTable.data.length; y++) {
				if($scope.upcomingReferralsTable.data[y].id === refId) {
					$scope.upcomingReferralsTable.data.splice(y, 1);
				}
			}
		});
	}
}]);