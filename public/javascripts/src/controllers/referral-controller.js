//Referral controller
app.controller('ReferralController', ["$scope", "$http", function ($scope, $http) {
	$scope.referral = {};
	$scope.referral.client = {};
	$scope.referral.client.goals = {};
	$scope.addReferral = function (referral) {
		//Add client and get ID
		$http.post("/json/client", referral.client).success(function (data, status, headers) {
			//Get client information and ID
			$http.get(headers("LOCATION")).success(function (data){
				//Prepare referral data
				referral.clientId = data.data.id;
				referral.clientName = data.data.name;
				referral.creatorId = app.data.currentUserId;

				//Get fresh user/agent data
				$http.get("/json/user/" + referral.agentId).success(function (data, status, headers) {
					var user = data.data;
					user.referrals.push(referral);

					//Update user/agent with this referral
					$http.put("/json/user", user).success(function (data, status, headers) {
						//Redirect to home
						window.location = "/home";

					}).error(function (error) {
						console.log("User " + referral.client.userName + " could not be updated");
					});
				}).error(function (error) {
					console.log("User could not be found with userName: " + referral.client.userName);
				});
			});
		});
	};
	$scope.init = function () {
		//Parse params for refId
		if(window.location.href.match(/\/editReferral\/\d+$/)){
			var param = window.location.href.match(/\/editReferral\/\d+$/)[0];
			param = param.slice(param.indexOf("Referral/") + 9);
			$scope.editReferral(param);
		}
	};

	//Gather referral to be updated
	$scope.editReferral = function (refId) {
		$http.get("/json/referral/" + refId).success(function ( data, status, headers) {
			$scope.referral = data.data;
			$http.get("/json/client/" + $scope.referral.clientId).success(function ( data, status, headers) {
				$scope.referral.client = data.data;
				parseGoalsString($scope.referral.client.goalsString);
				delete referral.client.goalsString;
			});
		});
	};

	//Have form submit to different method that can run the PUT instead of add (POST)
	$scope.updateReferral = function (referral) {
		$http.put("/json/referral", referral).success(function ( data, status, headers) {
			referral.client.goalsString = "";
			for(var goal in referral.client.goals) {
				if(referral.client.goals.hasOwnProperty(goal) && referral.client.goals[goal] === true) {
					referral.client.goalsString += " " + goal;
				}
			}
			$http.put("/json/client", referral.client).success(function ( data, status, headers) {
				window.location.href = "/action/referral";
			});
		});
	};

	function parseGoalsString (goalsString) {
		var goalArray = goalsString.split(" ");
		for(var x = 0; x < goalArray.length; x++) {
			var goal = goalArray[x];
			goal = goal.trim();
			if(goal !== "" && goal != " ") {
				$scope.referral.client.goals[goal] = true;
			}
		}
	}

	$scope.init();
}]);