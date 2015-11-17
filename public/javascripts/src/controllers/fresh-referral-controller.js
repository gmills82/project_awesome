//Fresh Referral Controller
app.controller('FreshReferralController', [
    "$scope",
    "$rootScope",
    "$http",
    '$log',
    "ngTableParams",
    "$filter",
    'referralService',
    'events',
    function ($scope, $rootScope, $http, $log, ngTableParams, $filter, referralService, events) {
        $scope.referrals = [];
        $scope.freshRefTypes = [{'title': 'No Filter', 'id': ''}];
        $scope.arr=[];

        var
            /**
             Property that determines whether or not the page has been rendered
             @type {boolean}
             */
            hasRendered = false;

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

                        orderedData = orderedData || [];

                        //Pass out total to larger scope
                        $scope.freshReferraltotal = orderedData.length;
                        params.total(orderedData.length);

                        // Look up the referral types from the service to populate the filter dropdown
                        if (!hasRendered) {
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
                        }
                        hasRendered = true;

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

        $rootScope.$on(events.REFERRAL_UPDATED, function (event, args) {
            if (!args || !args.id) {
                return;
            }
            for (var i = 0; i < $scope.freshReferralsTable.data.length; i++) {
                if ($scope.freshReferralsTable.data[i].id === args.id) {
                    $scope.freshReferralsTable.data[i] = args;
                }
            }
        });
    }
]);