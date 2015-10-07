app.controller('TeamReferralsController', [
    '$scope',
    '$filter',
    'ngTableParams',
    'referralService',
    function ($scope, $filter, ngTableParams, referralService) {

        $scope.referrals = [];
        $scope.referralTypes = [{'title': 'No Filter', 'id': ''}];

        $scope.teamReferralsTable = new ngTableParams(
            {
                page: 1,
                count: 10,
                sorting: {
                    nextStepTimestamp: 'desc'
                }
            },
            {
                getData: function($defer, params) {
                    referralService.getTeamReferrals(app.data.currentUserId, function (data) {

                        var filteredData = params.filter() ?
                            $filter('filter')(data.data.referrals, params.filter()) :
                            data.data;

                        var orderedData = params.sorting() ?
                            $filter('orderBy')(filteredData, params.orderBy()) :
                            filteredData;

                        orderedData = orderedData || [];
                        params.total(orderedData.length);

                        // Look up the referral types from the service to populate the filter dropdown
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

                        $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
                    });
                }
            }
        );
    }
]);