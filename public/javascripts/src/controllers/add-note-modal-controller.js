app.controller('AddNoteModalController', [
    "$scope",
    "$modalInstance",
    "referral",
    "referralService",
    function ($scope, $modalInstance, referral, referralService) {

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

        referralService.get(referral.id, function (data) {
            $scope.referral = data;
        });
    }
]);