app.controller('ForgotPasswordModalController', [
    "$scope",
    "$modalInstance",
    function ($scope, $modalInstance) {

        /**
         Closes the modal and takes no action
         */
        $scope.close = function () {
            $modalInstance.dismiss();
        };
    }
]);