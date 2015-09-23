app.directive('addNoteButton', [function() {
    return {
        restrict: 'AE',
        controller: 'AddNoteController',
        templateUrl: '/assets/javascripts/src/views/add-note-button.html',
        scope: {
            referral: '=',
            type: '@'
        },
        link: function (scope) {
            console.log(scope);
        }
    }
}]);