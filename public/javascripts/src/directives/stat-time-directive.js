app.directive('statTimeDirective', ['$timeout', function($timeout) {
    return {
        restrict: 'A',
        controller: 'StatsController',
        templateUrl: "/assets/javascripts/src/views/stat-time-view.html",
        link: function(scope, element, attrs, StatsController) {

            /**
             Toggles the date scope with the provided number of days.
             Sets the active class on the current selected element.

             @param     {Object}    [$event]    Event
             @param     {Number}    [days]      Days previous from today
             */
            scope.toggleDate = function ($event, days) {
                element.find('.stat-button.active').removeClass('active');
                $($event.target).addClass('active');
                if (!days) {
                    scope.fromDate = 0;
                    return;
                }
                scope.fromDate = moment().subtract(days, 'days').toDate().getTime();
            };

            // Set the default to a week
            $timeout(function () {
                element.find('.stat-button').eq(0).trigger('click');
            });
        }
    }
}]);