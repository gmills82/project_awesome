//Fresh Referral Controller
app.controller('FreshReferralController', [
    "$scope",
    "$http",
    '$log',
    "ngTableParams",
    "$filter",
    'referralService',
    'events',
    function ($scope, $http, $log, ngTableParams, $filter, referralService, events) {
        $scope.referrals = [];
        $scope.freshRefTypes = [{'title': 'No Filter', 'id': ''}];
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
            $scope.freshReferralsTable = new ngTableParams({
                page: 1,            // show first page
                count: 10,
                sorting: {
                    nextStepTimestamp: 'desc'
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
                        $scope.freshReferraltotal = orderedData.length;
                        params.total(orderedData.length);

                        // Look up the referral types from the service to populate the filter dropdown
                        referralService.getReferralTypes(function (error, data) {
                            if (error || !data) {
                                $log.error("Error getting referral types.", error || "No data returned from the service.");
                                return;
                            }
                            angular.forEach(data, function (type) {
                                $scope.freshRefTypes.push({
                                    'id': type.id,
                                    'title': type.title
                                });
                            });
                        });

                        //Resolve data gathering
                        $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
                    });
                }
            });

        };

        $scope.$on(events.REFERRAL_DELETED, function (event, args) {
            var referral = args.referral;
            for(var y = 0; y < $scope.freshReferralsTable.data.length; y++) {
                if($scope.freshReferralsTable.data[y].id === referral.id) {
                    $scope.freshReferralsTable.data.splice(y, 1);
                }
            }
        });
    }
]);