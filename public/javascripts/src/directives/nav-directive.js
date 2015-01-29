app.directive('navAction', ['$timeout', function(timer) {
	return {
		restrict: 'A',
		controller: 'ActionController',
		scope: {},
		link: function(scope, element, attrs, ActionController) {
			function checkActiveState() {
				var actionUrl = window.location.pathname;
				var navLinks = $(element).find('a');
				var currentActive = $('ul.nav a[href="/home"]').parents('li');

				navLinks.each(function(index,ele) {
					if($(ele).attr('href') === actionUrl) {
						$(ele).addClass('active');
						$(element).addClass('active');
						currentActive.removeClass('active');
					}
				});
			}

			scope.category = attrs.navCategory;
			scope.actions = scope.getActions(attrs.navCategory);
			timer(checkActiveState, 1000);
		},
		templateUrl: "/assets/javascripts/src/views/nav-view.html"
	};
}]);