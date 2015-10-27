app.controller('ClientNotesController', [
    "$scope",
    "clientService",
    "userService",
    "events",
    function ($scope, clientService, userService, events) {

        /** The records list for the client */
        $scope.records = undefined;

        // Make the service request to get the records
        clientService.getRecords($scope.clientid, function (data) {
            if (!data || !data.records) {
                return $scope.error = "No notes found for this client";
            }
            $scope.records = data.records;
        });

        $scope.$on(events.REFERRAL_NOTE_ADDED, function (event, args) {
            if (!$scope.records) {
                $scope.records = [];
            }
            if (!$scope.records[0].notes) {
                $scope.records[0].notes = [];
            }
            $scope.records[0].notes.unshift(args);
        });
    }
]);