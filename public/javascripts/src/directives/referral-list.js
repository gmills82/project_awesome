(function referralList() {

    app.controller('ReferralListController', [
        '$scope',
        '$rootScope',
        '$filter',
        '$timeout',
        'ngTableParams',
        'referralService',
        'events',
        function defineReferralListController($scope, $rootScope, $filter, $timeout, ngTableParams, referralService, events) {

            $scope.referralTypes = [{'title': 'No Filter', 'id': ''}];

            var
                /**
                 Property that determines whether or not the page has been rendered
                 @type {boolean}
                 */
                hasRendered = false,

                /**
                 Initializes the table view
                 */
                initializeTable = function () {
                    $scope.referralsTable = new ngTableParams(
                        {
                            page: 1,
                            count: 10,
                            sorting: {
                                nextStepTimestamp: 'asc'
                            }
                        },
                        {
                            getData: function ($defer, params) {

                                var data = $scope.ngModel;

                                var filteredData = params.filter() ?
                                    $filter('filter')(data, params.filter()) :
                                    data;

                                var orderedData = params.sorting() ?
                                    $filter('orderBy')(filteredData, params.orderBy()) :
                                    filteredData;

                                orderedData = orderedData || [];

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
                                            $scope.referralTypes.push({
                                                'id': type.id,
                                                'title': type.title
                                            });
                                        });
                                    });
                                }
                                hasRendered = true;
                                $defer.resolve(
                                    orderedData.slice(
                                        (params.page() - 1) * params.count(),
                                        params.page() * params.count()
                                    )
                                );
                            }
                        }
                    );
                };

            // Listen for deleted referrals and remove them from the table
            $scope.$on(events.REFERRAL_DELETED, function (event, args) {
                var referral = args.referral;
                for(var y = 0; y < $scope.referralsTable.data.length; y++) {
                    if($scope.referralsTable.data[y].id === referral.id) {
                        $scope.referralsTable.data.splice(y, 1);
                    }
                }
            });

            $rootScope.$on(events.REFERRAL_UPDATED, function (event, args) {
                if (!args || !args.id) {
                    return;
                }
                for (var i = 0; i < $scope.referralsTable.data.length; i++) {
                    if ($scope.referralsTable.data[i].id === args.id) {
                        $scope.referralsTable.data[i] = args;
                    }
                }
            });

            // Push the initialization of the table view to the end of the call stack to give the model enough
            // time to propagate to the scope.
            $timeout(initializeTable);
        }
    ]);

    app.directive('referralList', [
        function defineReferralList() {
            return {
                restrict: 'A',
                templateUrl: '/assets/javascripts/src/views/referral-list.html',
                controller: 'ReferralListController',
                scope: {
                    ngModel: '=',
                    listTitle: '@'
                }
            }
        }
    ]);
}());