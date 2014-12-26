//Base app setup
var app = angular.module("project_awesome", ["ngRoute"]);
app.data = {};
if(document.getElementById("baseContainer")){
	app.data.currentUserId = document.getElementById("baseContainer").getAttribute("data-user");
}
