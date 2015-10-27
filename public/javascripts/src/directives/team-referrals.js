app.directive('teamReferrals', [
    function () {
        return {
            restrict: 'A',
            controller: 'TeamReferralsController',
            templateUrl: '/assets/javascripts/src/views/team-referrals.html'
        }
    }
]);