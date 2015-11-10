(function defineNextStepsPopover () {

    app.controller('NextStepsPopoverController', [
        '$scope',
        function defineNextStepsPopoverController($scope) {

            // Get the current date to display from the provided scope. This will be
            // our final date selected.
            var date = $scope.currentDate ? new Date(parseInt($scope.currentDate)) : new Date();

            // Set up the timepicker options
            $scope.hstep = 1;
            $scope.mstep = 15;
            $scope.ismeridian = true;

            $scope.isOpen = false;

            // Set the default time of the timepicker to noon.
            // Something odd, the scope of the model used in the timestamp needs to be
            // referenced by $parent. https://github.com/angular-ui/bootstrap/issues/1141
            var timestamp = new Date();
            timestamp.setHours(12);
            timestamp.setMinutes(0);
            $scope.timestamp = timestamp;

            // Set the default date to be used for the datepicker
            $scope.defaultDate = (date.getMonth() + 1) + '/' + date.getDate() + '/' + date.getFullYear();

            // When the popover is shown, initialize the datepicker. If the user changes
            // the date, update the final date property.
            $scope.popoverLinkClicked = function () {
                $('#next-steps-datepicker')
                    .datepicker()
                    .on('changeDate', function (e) {
                        date.setFullYear(e.date.getFullYear());
                        date.setMonth(e.date.getMonth());
                        date.setDate(e.date.getDate());
                    });
            };

            // Action to take when the confirm button is clicked. Parses the final date
            // and uses it on the edit screen.
            $scope.confirmClicked = function () {
                date.setHours($scope.timestamp.getHours(), $scope.timestamp.getMinutes());
                location.href = '/editReferral/' + $scope.referralId + '#?nextsteps=' + date.getTime()
            };

            // Action to take when the cancel button is clicked. Closes the popover and
            // removes all edits to the pickers.
            $scope.cancelClicked = function () {
                $scope.isOpen = false;
            };
        }
    ]);
    app.directive('nextStepsPopover', [
        function defineNextStepsPopoverDirective() {
            return {
                restrict: 'AE',
                templateUrl: '/assets/javascripts/src/views/next-steps-popover.html',
                controller: 'NextStepsPopoverController',
                transclude: true,
                scope: {
                    currentDate: '@',
                    referralId: '@'
                }
            }
        }
    ]);
}());