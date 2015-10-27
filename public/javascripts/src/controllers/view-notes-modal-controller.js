app.controller('ViewNotesModalController', [
    "$scope",
    "$modalInstance",
    "referral",
    function ($scope, $modalInstance, referral) {

        $scope.referral = referral;

        /**
         Closes the modal and takes no action
         */
        $scope.done = function () {
            $modalInstance.dismiss();
        };
    }
]);