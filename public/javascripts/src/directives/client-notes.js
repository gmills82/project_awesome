app.directive('clientNotes', [function() {
    return {
        restrict: 'AE',
        controller: 'ClientNotesController',
        templateUrl: '/assets/javascripts/src/views/client-notes.html',
        scope: {
            clientid: '='
        }
    }
}]);