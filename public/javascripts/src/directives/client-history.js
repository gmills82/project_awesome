(function defineClientHistory() {
    app.controller('ClientHistoryController', [
        '$scope',
        'clientService',
        function defineClientHistoryController($scope, clientService) {
            clientService.getRecords($scope.clientId, function (data) {
                data = data || {};
                if (data.records && data.records.length > 0) {
                    $scope.referral = data.records[0];
                }
            });
        }
    ]);
    app.directive('clientHistory', [
        function defineClientHistoryDirective() {
            return {
                restrict: 'AE',
                controller: 'ClientHistoryController',
                scope: {
                    clientId: '='
                }
            }
        }
    ]);
}());