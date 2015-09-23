app.controller('DeleteReferralController', [
    "$scope",
    "$modal",
    "referralService",
    "events",
    function ($scope, $modal, referralService, events) {

        /**
         Presents the modal allowing the user to delete a referral

         @param     {Object}    $event      Click event
         */
        $scope.showDeleteReferralModal = function ($event) {
            $event.preventDefault();
            var instance = $modal.open({
                animation: true,
                controller: 'DeleteReferralModalController',
                templateUrl: '/assets/javascripts/src/views/delete-referral-modal.html',
                resolve: {
                    referral: function () {
                        return $scope.referral;
                    }
                }
            });

            // Send the request to delete the referral. Also, in parallel, emit the notification that the referral has
            // been deleted so other listeners can update immediately without having to wait for the response from the
            // service.
            instance.result.then(function (referral) {
                if (referral) {
                    referralService.deleteById(referral.id);
                    $scope.$emit(events.REFERRAL_DELETED, {referral: referral});
                }
            });
        }
    }
]);