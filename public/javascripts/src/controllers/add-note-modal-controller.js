app.controller('AddNoteModalController', [
    "$scope",
    "$modalInstance",
    "referral",
    function ($scope, $modalInstance, referral) {

        /**
         Closes the modal and takes no action
         */
        $scope.cancel = function () {
            $modalInstance.dismiss();
        };

        /**
         Confirms the addition of the note and sends the data back to the original controller
         */
        $scope.confirm = function () {
            $modalInstance.close({
                referral: referral,
                note: $scope.note
            });
        };
    }
]);