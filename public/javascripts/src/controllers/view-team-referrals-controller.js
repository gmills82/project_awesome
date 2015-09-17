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

		$scope.downloadReportUrl = '/agent/' + app.data.currentUserId + '/referrals';

		$scope.viewTeamReferrals = new ngTableParams({
			page: 1,            // show first page
			count: 10,
			sorting: {
				lastEditedDate: 'desc'
			}
		}, {
			getData: function($defer, params) {

                var requestParameters = {},
                    filter = params.filter();
                for (var key in filter) {
                    if (filter.hasOwnProperty(key)) {
                        requestParameters[key] = filter[key];
                    }
                }
                requestParameters.sort = params.orderBy();
                requestParameters.offset = (params.page() - 1) * params.count();
                requestParameters.limit = params.count();

				$http({
                    "method": "GET",
                    "url": "/json/referrals/team/" + app.data.currentUserId,
                    params: requestParameters
                }).success(function (data){

                    var referrals = data.data.referrals,
                        total = data.data.total;

					//Pass out total to larger scope
					$scope.total = total;
					params.total(total);

					//Create scope for RefType filter
					angular.forEach(referrals, function(item){
						if (inArray(item.refType, $scope.arr) === -1) {
							$scope.arr.push(item.refType);
							$scope.teamRefs.push({
								'id': item.refType,
								'title': item.refType
							});
						}
					});

					//Resolve data gathering
					$defer.resolve(referrals);
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