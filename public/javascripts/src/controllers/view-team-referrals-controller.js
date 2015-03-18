//Referrals By Creator Controller
app.controller('ViewTeamReferralsController', ["$scope", "$http", "ngTableParams", "$filter", function ($scope, $http, ngTableParams, $filter){
	$scope.referrals = [];
	$scope.teamRefs = [{'title': 'No Filter', 'id': ''}];
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
		$scope.viewTeamReferrals = new ngTableParams({
			page: 1,            // show first page
			count: 10,
			sorting: {
				lastEditedDate: 'desc'
			}
		}, {
			getData: function($defer, params) {
				$http({"method": "GET", "url": "/json/referrals/team/" + app.data.currentUserId}).success(function (data){
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

					//Create scope for RefType filter
					angular.forEach(orderedData, function(item){
						if (inArray(item.refType, $scope.arr) === -1) {
							$scope.arr.push(item.refType);
							$scope.teamRefs.push({
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
			for(var y = 0; y < $scope.viewTeamReferrals.data.length; y++) {
				if($scope.viewTeamReferrals.data[y].id === refId) {
					$scope.viewTeamReferrals.data.splice(y, 1);
				}
			}
		});
	}
}]);