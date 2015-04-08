//Fresh Referral Controller
app.controller('RecentProfilesController', ["$scope", "$http", "ngTableParams", "$filter", function ($scope, $http, ngTableParams, $filter){
	this.init = function () {
		//Ng-table setup
		$scope.recentProfilesTable = new ngTableParams({
			page: 1,            // show first page
			count: 5,
			sorting: {
				createdDatePretty: 'desc'
			}
		}, {
			counts: [],
			getData: function($defer, params) {
				$http({"method": "GET", "url": "/json/recentProfiles/" + app.data.currentUserId}).success(function (data){
					//Pretty dates for profiles
					var profiles = data.data;
					for(var q = 0; q < profiles.length; q++) {
						date = new Date(profiles[q].createdDate);
						profiles[q].createdDatePretty = (date.getMonth() + 1) + "/" + date.getDate() + "/" + date.getFullYear();
					}

					//Then sort
					var orderedData = params.sorting() ?
						$filter('orderBy')(profiles, params.orderBy()) :
						profiles;

					//Pass out total to larger scope
					$scope.recentProfilesTotal = orderedData.length;
					params.total(orderedData.length);

					//Resolve data gathering
					$defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
				});
			}
		});
	}
}]);