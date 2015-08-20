//Base app setup
var app = angular.module("project_awesome", ["ngRoute", "validation.match", "ngTable", 'ui.bootstrap']);
app.data = {};

/**
 Date format filter for Angular templates. Allows us to keep a consistent date format across
 the entire application.
 */
app.filter('formatPrettyDate', function () {
    return function (item) {
        return moment(item).format("YYYY-MM-DD h:mm a");
    };
    //var dateFormat = "yyyy-MM-dd h:mm a";
    //
	//return function (input) {
     //   return input.toUpperCase();
     //   //return moment(input).format(dateFormat);
	//}
});
if(document.getElementById("baseContainer")){
	app.data.currentUserId = document.getElementById("baseContainer").getAttribute("data-user");
}
