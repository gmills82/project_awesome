app.directive('addNoteButton', [function() {
    return {
        restrict: 'AE',
        controller: 'AddNoteController',
        templateUrl: '/assets/javascripts/src/views/add-note-button.html',
        scope: {
            referral: '='
        },
        link: function (scope) {
            $('#addNoteModal').on('show.bs.modal', function (event) {
                var button = $(event.relatedTarget); // Button that triggered the modal
                var recipient = button.data('referral'); // Extract info from data-*
                var modal = $(this);
                //modal.find('.btn-primary').click(function () {
                //    scope.deleteReferral(recipient);
                //    modal.modal('hide');
                //});
            });
        }
    }
}]);