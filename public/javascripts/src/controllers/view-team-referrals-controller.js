//Referrals By Creator Controller
app.controller('ViewTeamReferralsController', [
    "$scope",
    "$http",
    '$log',
    "ngTableParams",
    'referralService',
    'events',
    function ($scope, $http, $log, ngTableParams, referralService, events) {
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

                        // Look up the referral types from the service to populate the filter dropdown
                        referralService.getReferralTypes(function (error, data) {
                            if (error || !data) {
                                $log.error("Error getting referral types.", error || "No data returned from the service.");
                                return;
                            }

                            // Why do we have to loop through each referral type and push it into the array here? Concatting
                            // the teamRefs array with the data object didn't work.
                            angular.forEach(data, function (type) {
                                $scope.teamRefs.push({
                                    'id': type.id,
                                    'title': type.title
                                });
                            });
                        });

                        //Resolve data gathering
                        $defer.resolve(referrals);
                    });
                }
            });
        };

        $scope.$on(events.REFERRAL_DELETED, function (event, args) {
            var referral = args.referral;
            for(var y = 0; y < $scope.viewTeamReferrals.data.length; y++) {
                if($scope.viewTeamReferrals.data[y].id === referral.id) {
                    $scope.viewTeamReferrals.data.splice(y, 1);
                }
            }
        });
    }
]);