app.controller('StatsController', ['$scope', function ($scope) {

    /** The end date to use */
    $scope.toDate = new Date().getTime();

    /**
     Toggles the date scope with the provided number of days

     @param     {Number}    [days]      Days previous from today
     */
    $scope.toggleDate = function (days) {
        if (!days) {
            $scope.fromDate = 0;
            return;
        }
        $scope.fromDate = moment().subtract(days, 'days').toDate().getTime();
    };

    // Set the default to all time
    $scope.toggleDate();
}]);