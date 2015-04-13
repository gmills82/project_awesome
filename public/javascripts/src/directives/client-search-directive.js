app.directive('clientSearchDirective', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'ClientController',
		link: function(scope, element, attrs, ClientController) {
			var CHAR_TYPED_BEFORE_SEARCH = 3;
			//Referral parent scope
			if(typeof(scope.referral) != 'undefined') {
				scope.referralView = true;
			}
			//Profile parent scope
			if(typeof(scope.profile) != 'undefined') {
				scope.profileView = true;
			}

			//TODO: Call ajax endpoint to get
			scope.requestClientHistory = function (client) {
				console.log("history requested");
				//Call ajax endpoint for history
				//Add to scope.history
			};

			function init() {
				var searchBar = $(element).find('form input');
				scope.$watch("search", function (searchVal) {
					if(typeof(searchVal) !== 'undefined' && searchVal.length >= CHAR_TYPED_BEFORE_SEARCH) {
						ClientController.queryClients(searchVal);
					}else {
						scope.clients = [];
					}
				});
			}

			timer(init, 1000);
		},
		templateUrl: "/assets/javascripts/src/views/client-search-view.html"
	};
}]);