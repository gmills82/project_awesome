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

            instance.result.then(function (referral) {
                $scope.$emit(events.REFERRAL_DELETED, {referral: referral});
            });
        }
    }
]);