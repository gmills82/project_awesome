app.directive('statTimeDirective', ['$timeout', function($timeout) {

    var
        _formatWeekRange = function (fromDate, toDate) {
            var format = "MM/DD/YYYY";
            return fromDate.format(format) + ' to ' + toDate.format(format);
        },

        _formatMonthRange = function (fromDate, toDate) {
            var format = "MMMM";
            return toDate.format(format);
        },

        _formatYearRange = function (fromDate, toDate) {
            var format = "YYYY";
            return toDate.format(format);
        },

        _formatAllRange = function (fromDate, toDate) {
            return "All Time";
        };

    return {
        restrict: 'A',
        controller: 'StatsController',
        transclude: true,
        templateUrl: "/assets/javascripts/src/views/stat-time-view.html",
        link: function(scope, element, attrs, StatsController) {

            /**
             Toggles the date scope with the provided number of days.
             Sets the active class on the current selected element.

             @param     {Object}    [$event]    Event
             @param     {String}    [type]      Type of time
             */
            scope.toggleDate = function ($event, type) {
                element.find('.stat-button.active').removeClass('active');
                $($event.target).addClass('active');

                var toDate = moment(),
                    fromDate,
                    timeDisplay;
                switch (type) {
                    case 'week' :
                        fromDate = moment().startOf('week');
                        timeDisplay = _formatWeekRange(fromDate, toDate);
                        break;
                    case 'month' :
                        fromDate = moment().startOf('month');
                        timeDisplay = _formatMonthRange(fromDate, toDate);
                        break;
                    case 'year' :
                        fromDate = moment().startOf('year');
                        timeDisplay = _formatYearRange(fromDate, toDate);
                        break;
                    default :
                        fromDate = moment(0);
                        timeDisplay = _formatAllRange(fromDate, toDate);
                }

                scope.toDate = toDate.toDate().getTime();
                scope.fromDate = fromDate.toDate().getTime();
                scope.timeDisplay = timeDisplay;

                //if (!days) {
                //    scope.fromDate = 0;
                //    return;
                //}
                //scope.fromDate = moment().subtract(days, 'days').toDate().getTime();
            };

            // Set the default to a week
            $timeout(function () {
                element.find('.stat-button').eq(0).trigger('click');
            });
        }
    }
}]);