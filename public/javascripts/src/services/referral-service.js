//TODO: Make a factory to create the referral service. Should have crud methods and function params for callbacks
app.factory('referralService', function() {
	var service = {};
	service.moop = function () {
		console.log("moop");
	};

	return service;
});