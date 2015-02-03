app.directive('clientSearchDirective', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'ClientController',
		link: function(scope, element, attrs, ClientController) {
			function init() {
				var searchBar = $(element).find('form input');
				scope.$watch("search", function (searchVal) {
					if(typeof(searchVal) !== 'undefined' && searchVal.length > 3) {
						ClientController.queryClients(searchVal);
					}
				});
			}

			timer(init, 1000);
		},
		templateUrl: "/assets/javascripts/src/views/client-search-view.html"
	};
}]);