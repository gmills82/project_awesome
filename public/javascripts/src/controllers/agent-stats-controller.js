app.controller('AgentStatsController', ["$scope", "$http", function($scope, $http) {

    /**
     Populates the data for the stat modules

     @private
     @param     {Object}    [data]      The data to populate the modules with
     */
    var _populateData = function (data) {
        data = (data && data.data) ? data : {data: {}};
        var totalReferrals = parseFloat(data.data.totalReferrals || 0),
            totalProductiveReferrals = parseFloat(data.data.totalProductiveReferrals || 0),
            percentageProductiveReferrals = Math.round((totalProductiveReferrals / totalReferrals) * 100);
        if (isNaN(percentageProductiveReferrals)) {
            percentageProductiveReferrals = 0;
        }
        $scope.totalReferrals = totalReferrals;
        $scope.percentageProductiveReferrals = percentageProductiveReferrals;
        $scope.mostTotalClients = data.data.mostTotalClients;
        $scope.mostProductiveReferrals = data.data.mostProductiveReferrals;
        $scope.highestPercentageProductiveReferrals = data.data.highestPercentageProductiveReferrals;

        $scope.getProductiveReferrals = function () {
            var desc = (totalProductiveReferrals === 1) ? "Referrals Were Productive" : "Referrals Were Productive";
            return totalProductiveReferrals + " " + desc;
        };

        $scope.getProductiveReferralsPercentage = function () {
            return percentageProductiveReferrals + "% of referrals were productive";
        };
    };

    // Populate the data right away to minimize height jumping
    _populateData();

    // Watch the fromDate scope property and update the stats if it changes
    $scope.$watch('fromDate', function() {
        var fromDate = parseFloat($scope.fromDate),
            toDate = parseFloat($scope.toDate);
        $http({"method": "GET", "url": "/stats/agent/" + app.data.currentUserId + "/" + fromDate + "/" +  toDate})
            .success(function (data) {
                _populateData(data);
            });
    });
}]);