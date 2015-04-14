//Base app setup
var app = angular.module("project_awesome", ["ngRoute", "validation.match", "ngTable", 'ui.bootstrap']);
app.data = {};
if(document.getElementById("baseContainer")){
	app.data.currentUserId = document.getElementById("baseContainer").getAttribute("data-user");
}
