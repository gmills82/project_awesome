//Referral controller
app.controller('ReferralController', ["$scope", "$http", "referralService", function ($scope, $http, referralService) {
	//Reference to original controller scope
	var that = this;
	/**
	 * Private helper method for getting todays date in the correct format, yyyy-mm-dd
	 */
	this.getTodaysDate = function() {
		var today = new Date();
		var year = today.getFullYear();
		var month = today.getMonth() + 1;
		if(month.toString().length < 2) {
			month = "0" + month;
		}
		var day = today.getDate();

		return year + "-" + month + "-" + day
	};

	//Scope preperation
	$scope.referral = {};
	$scope.referral.client = {};
	$scope.referral.client.goals = {};
	$scope.refStatus = [
		{
			"status": "OPEN"
		},
		{
			status: "CLOSED"
		},
		{
			status: "CANCELLED"
		}
	];

	$scope.referral.nextStepDate = this.getTodaysDate();

	referralService.moop();

	//Prepare scope for edit view if the url param refId is present in the url
	if(window.location.href.match(/\/editReferral\/\d+$/)){
		var param = window.location.href.match(/\/editReferral\/\d+$/)[0];
		param = param.slice(param.indexOf("Referral/") + 9);
		//Prepares edit form view related business logic
		$scope.editReferral(param);
	}

	//TODO: Form submission function which uses a referral service's methods. All logic involving the page and the form should be in this function. Everything above this line is appropriate for the controller because it is view logic. Everything below addReferralForm is mixed non view business logic and view based business logic and should be refactored to not be mixed, leaving only view based business logic
	$scope.addReferralFormSubmission = function (referral) {
		//Referral data adjustments based on views
		//Defaults add next step date date box to today
		referral.lastEditedDate = that.getTodaysDate();
		//Append time to nextStepDate
		referral.nextStepDate += " " + referral.nextStepTime;

		//TODO: Call referralService with a flag to use an update on the client if this is edit view
		referral.updateExistingClientFlag = true;
		referralService.post(referral, function () {
			window.location = "/home";
		});
	};

	$scope.addReferral = function (referral) {
		//Add client and get ID
		$http.post("/json/client", referral.client).success(function (data, status, headers) {
			//Get up to the minute client information and ID
			$http.get(headers("LOCATION")).success(function (data){
				//Prepare client information on referral data
				referral.clientId = data.data.id;
				referral.clientName = data.data.name;
				referral.creatorId = app.data.currentUserId;

				//TODO: View related business logic
				//Referral data adjustments based on views
				//Defaults add next step date date box to today
				referral.lastEditedDate = that.getTodaysDate();
				//Append time to nextStepDate
				referral.nextStepDate += " " + referral.nextStepTime;

				//TODO: We will always want to do this regardless of the view when saving the referral. Non view related
				//Get fresh user/agent data
				$http.get("/json/user/" + referral.agentId).success(function (data, status, headers) {
					var user = data.data;
					//Add referral to the user
					user.referrals.push(referral);

					//Update user/agent with this referral
					$http.put("/json/user", user).success(function (data, status, headers) {
						//TODO: View related redirection
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

	//Prepares the edit referral form view
	$scope.prepareEditReferralForm = function (refId) {
		referralService.get(refId, function (referral) {

			//View related business logic preparing the edit view
			$scope.referral = referral;
			$scope.referral.lastEditedDate = that.getTodaysDate();
			//Append time to nextStepDate
			referral.nextStepDate += " " + referral.nextStepTime;
			//Set default status to OPEN
			for(var x = 0; x < $scope.refStatus.length; x++) {
				if($scope.referral.status == $scope.refStatus[x].status) {
					$scope.referral.status = $scope.refStatus[x];
				}
			}
		});
	};

	//Handles view related edit referral view form submission
	$scope.editReferralFormSubmission = function (referral) {
		referralService.put(referral, function () {
			window.location.href = "/action/referral";
		})
	};

}]);