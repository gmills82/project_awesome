app.controller('ViewNotesController', [
    "$scope",
    "$modal",
    "referralService",
    "events",
    function defineViewNotesController ($scope, $modal, referralService, events) {

        /**
         Presents the modal allowing the user to view the notes of a referral

         @param     {Object}    $event      Click event
         */
        $scope.showViewNotesModal = function ($event) {
            $event.preventDefault();

            referralService.get($scope.referral.id, function (data) {
                $modal.open({
                    animation: true,
                    controller: 'ViewNotesModalController',
                    templateUrl: '/assets/javascripts/src/views/view-notes-modal.html',
                    resolve: {
                        referral: function () {
                            return data;
                        }
                    }
                });
            });
        };
    }
]);