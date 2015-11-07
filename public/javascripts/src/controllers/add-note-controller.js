app.controller('AddNoteController', [
    "$scope",
    "$rootScope",
    "$modal",
    "referralService",
    "events",
    function ($scope, $rootScope, $modal, referralService, events) {

        /**
         Presents the modal allowing the user to add a note to the referral

         @param     {Object}    $event      Click event
         */
        $scope.showAddNoteModal = function ($event) {
            $event.preventDefault();
            var instance = $modal.open({
                animation: true,
                controller: 'AddNoteModalController',
                templateUrl: '/assets/javascripts/src/views/add-note-modal.html',
                resolve: {
                    referral: function () {
                        return $scope.referral;
                    }
                }
            });

            // Submits the note to the referral and closes the modal.
            instance.result.then(function (data) {
                if (data) {
                    referralService.addNoteToReferral(data.note, data.referral.id, function (data) {
                        $rootScope.$emit(events.REFERRAL_NOTE_ADDED, data.data);
                    });
                }
            });
        }
    }
]);