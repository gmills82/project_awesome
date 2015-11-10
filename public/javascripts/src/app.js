//Base app setup
var app = angular.module("project_awesome", [
    "ngRoute",
    "validation.match",
    "ngTable",
    'ui.bootstrap',
    'ngSanitize'
]);
app.data = {};

// Date format filter for Angular templates. Allows us to keep a consistent date format across
// the entire application.
app.filter('formatDate', function () {
    return function (item) {
        if (!item) {
            item = new Date();
        }
        return moment(item).format("YYYY-MM-DD h:mm a");
    };
});

// Event constants that are emitted and listened on.
app.constant('events', {
    'REFERRAL_DELETED': 'referralDeleted',
    'REFERRAL_NOTE_ADDED': 'referralNoteAdded'
});

if(document.getElementById("baseContainer")){
	app.data.currentUserId = document.getElementById("baseContainer").getAttribute("data-user");
}
