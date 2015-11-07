(function defineActionDropdown() {
    app.controller('ActionDropdownController', [
        '$scope',
        'clientService',
        function defineActionDropdownController($scope, clientService) {
            if ($scope.clientId && !$scope.referral) {
                clientService.getRecords($scope.clientId, function (data) {
                    data = data || {};
                    if (data.records && data.records.length > 0) {
                        $scope.referral = data.records[0];
                    }
                });
            }
        }
    ]);
    app.directive('actionDropdown', [
        function defineActionDropdownDirective () {
            return {
                restrict: 'AE',
                templateUrl: '/assets/javascripts/src/views/action-dropdown.html',
                controller: 'ActionDropdownController',
                scope: {
                    referral: '=',
                    clientId: '='
                }
            }
        }
    ]);
}());