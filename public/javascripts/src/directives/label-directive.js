app.directive('statusLabel', ['$timeout', function(timer) {
	return {
		restrict: 'E',
		scope: {
			status: '=status'
		},
		link: function (scope) {
			switch(scope.status) {
				case "CLOSED":
					scope.labelClass = "success";
					break;
				case "CANCELLED":
					scope.labelClass = "danger";
					break;
				default:
					scope.labelClass = "info";
					break;
			}
		},
		template:  '<span class="label label-{{labelClass}}">{{status}}</span>'
	}
}]);