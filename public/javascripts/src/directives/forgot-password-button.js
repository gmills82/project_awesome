(function defineForgotPasswordButton() {

    app.controller('ForgotPasswordButtonController', [
        '$scope',
        '$modal',
        function defineForgotPasswordButtonController($scope, $modal) {

            $scope.presentModal = function ($event) {
                $event.preventDefault();
                $modal.open({
                    animation: true,
                    controller: 'ForgotPasswordModalController',
                    templateUrl: '/assets/javascripts/src/views/forgot-password-modal.html'
                });
            };
        }
    ]);

    app.directive('forgotPasswordButton', [
        function defineForgotPasswordDirective() {
            return {
                restrict: 'AE',
                controller: 'ForgotPasswordButtonController',
                templateUrl: '/assets/javascripts/src/views/forgot-password-button.html'
            }
        }
    ]);
}());