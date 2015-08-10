app.directive('statsDirective', [function () {
    return {
        restrict: 'A',
        controller: 'StatsController',
        link: function(scope, element, attrs, StatsController) {

        }
    }
}]);