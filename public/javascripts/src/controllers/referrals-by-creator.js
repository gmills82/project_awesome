//Referrals By Creator Controller
app.controller('ReferralsByCreatorController', [
    "$scope",
    "$http",
    "ngTableParams",
    "$filter",
    "events",
    'referralService',
    function ($scope, $http, ngTableParams, $filter, events, referralService) {
        $scope.referrals = [];
        $scope.recentRefTypes = [{'title': 'No Filter', 'id': ''}];
        $scope.arr=[];

        var
            /**
             Property that determines whether or not the page has been rendered
             @type {boolean}
             */
            hasRendered = false;

        this.init = function () {
            //Setup ng-table
            $scope.refByCreator = new ngTableParams({
                page: 1,            // show first page
                count: 10,
                sorting: {
                    lastEditedDate: 'desc'
                }
            }, {
                counts: [10, 15, 25, 50],
                getData: function($defer, params) {
                    $http({"method": "GET", "url": "/json/referrals/creator/" + app.data.currentUserId}).success(function (data){
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

                        // Look up the referral types from the service to populate the filter dropdown
                        if (!hasRendered) {
                            referralService.getReferralTypes(function (error, data) {
                                if (error || !data) {
                                    $log.error("Error getting referral types.", error || "No data returned from the service.");
                                    return;
                                }
                                angular.forEach(data, function (type) {
                                    $scope.recentRefTypes.push({
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
            for(var y = 0; y < $scope.refByCreator.data.length; y++) {
                if($scope.refByCreator.data[y].id === referral.id) {
                    $scope.refByCreator.data.splice(y, 1);
                }
            }
        });
    }
]);