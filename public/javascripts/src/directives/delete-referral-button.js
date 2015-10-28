app.directive('deleteReferralButton', [function() {
    return {
        restrict: 'AE',
        controller: 'DeleteReferralController',
        templateUrl: '/assets/javascripts/src/views/delete-referral-button.html',
        scope: {
            referral: '='
        }
    }
}]);