app.directive('map', ['$sce', function(sce) {
	return {
		restrict: 'A',
		link: function(scope, element, attrs) {
			var APIKEY = "?key=AIzaSyCTEUEU8NVULP8LxzZ04_9vZtjxGhjDhx0";
			var BASEURL = "https://www.google.com/maps/embed/v1/";
			var query = "&q=";

			//Needed for external Iframe urls
			scope.trustSrc = function(src) {
				return sce.trustAsResourceUrl(src);
			};

			//Copy passed in attributes over defaults
			var defaults = {
				height: "100%",
				width: "100%",
				address: "1600 Pennsylvania Avenue Northwest, Washington, DC 20500",
				mode: "place",
				zoom: "14"
			};

			attrs = angular.extend(defaults, attrs);

			//Set up query
			query += encodeURIComponent(attrs.address);
			zoomParam = "&zoom=" + attrs.zoom;

			//Returns on scope
			scope.mapUrl = BASEURL + attrs.mode + APIKEY + query + zoomParam;
			scope.width = attrs.width;
			scope.height = attrs.height;
		},
		templateUrl: "/assets/javascripts/src/views/map.html"
	};
}]);