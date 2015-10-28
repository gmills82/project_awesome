app.directive('viewNotesButton', [
    function defineViewNotesButton () {
        return {
            restrict: 'AE',
            controller: 'ViewNotesController',
            templateUrl: '/assets/javascripts/src/views/view-notes-button.html',
            scope: {
                referral: '='
            }
        }
    }
]);