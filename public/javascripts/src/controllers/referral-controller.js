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
	}
	//Parse params for refId
	//Gather referral to be updated
	//Have form submit to different method that can run the PUT instead of add (POST)
}]);