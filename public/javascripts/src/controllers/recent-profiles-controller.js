//Fresh Referral Controller
app.controller('RecentProfilesController', ["$scope", "$http", "$q", function ($scope, $http, $q){
	$scope.recentProfiles = [];
	$scope.childClients = [];
	$scope.predicate = "-createdDate";

	var profilePromise = getProfiles(app.data.currentUserId);
	clientPromise = profilePromise.then(function (profileList) {
		return getClients(profileList);
	});

	clientPromise.then(function (data){
		var clientList = data.child;
		var profiles = data.profiles;
		//match client to profile
		for(var y = 0; y < profiles.length; y++) {
			for(var z = 0; z < clientList.length; z++) {
				if(profiles[y].clientId === clientList[z].id) {
					profiles[y].client = clientList[z];
				}
			}
		}

		//Pretty dates for profiles
		for(var q = 0; q < profiles.length; q++) {
			date = new Date(profiles[q].createdDate);
			profiles[q].createdDatePretty = (date.getMonth() + 1) + "/" + date.getDate() + "/" + date.getFullYear();
		}

		$scope.recentProfiles = profiles;
	});

	function getProfiles(userId) {
		//get profile
		var getP = $http({"method": "GET", "url": "/json/recentProfiles/" + app.data.currentUserId}).then(function (data){
			//$http data object then our data object which nests it down to data
			return data.data.data;
		});
		return getP;
	}

	function getClients(profiles) {
		var lastChildP, childClients = [];

		function getChild(x) {
			var childP = $http({"method": "GET", "url": "/json/client/" + profiles[x].clientId}).then(function (data){
				childClients.push(data.data.data);
				if(x === profiles.length) {
					return {"child": childClients, "profiles": profiles};
				}
			});
			if(x === (profiles.length - 1)) {
				lastChildP = childP;
			}
			x++;
			if(x < profiles.length) {
				getChild(x);
			}
		}

		getChild(0);
		return lastChildP;
	}


}]);