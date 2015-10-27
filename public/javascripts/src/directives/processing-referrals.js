/**
 Displays a table of referrals that are currently being processed and assigned to the logged in user. Allows the user
 to take action on each of the referrals and filter, sort, and paginate through the results.
 */
(function processingReferrals() {

    app.controller('ProcessingReferralsController', [
        '$scope',
        'referralService',
        function defineProcessingReferralsController($scope, referralService) {

            // Look up the processing referrals and populate the model
            (function fetchProcessingReferrals() {
                referralService.getProcessingReferrals(app.data.currentUserId)
                    .success(function getProcessingReferralsSuccess(data) {
                        $scope.referrals = data.data;
                    })
                    .error(function getProcessingReferralsError(data) {
                        console.error(data);
                    });
            }());
        }
    ]);

    app.directive('processingReferrals', [
        function defineProcessingReferrals() {
            return {
                restrict: 'A',
                controller: 'ProcessingReferralsController',
                templateUrl: '/assets/javascripts/src/views/processing-referrals.html'
            }
        }
    ]);
}());