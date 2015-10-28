app.controller('DeleteReferralModalController', [
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
         Confirms the delete of the referral and sends the data back to the original controller
         */
        $scope.confirm = function () {
            $modalInstance.close(referral);
        };
    }
]);