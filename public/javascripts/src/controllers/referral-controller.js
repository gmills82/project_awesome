//Referral controller
app.controller('ReferralController', ["$scope", "$http", function ($scope, $http) {
	var that = this;
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

	//Prepare scope for edit view if the url param refId is present in the url
	if(window.location.href.match(/\/editReferral\/\d+$/)){
		var param = window.location.href.match(/\/editReferral\/\d+$/)[0];
		param = param.slice(param.indexOf("Referral/") + 9);
		//Prepares edit form view related business logic
		$scope.editReferral(param);
	}

	//TODO: Form submission function which uses a referral service's methods. All logic involving the page and the form should be in this function. Everything above this line is appropriate for the controller because it is view logic. Everything below addReferralForm is mixed non view business logic and view based business logic and should be refactored to not be mixed, leaving only view based business logic
	$scope.addReferralFormSubmission = function (referral) {
		//TODO: IF existing client then use update on client
	};

	$scope.addReferral = function (referral) {
		//Add client and get ID
		$http.post("/json/client", referral.client).success(function (data, status, headers) {
			//Get up to the minute client information and ID
			$http.get(headers("LOCATION")).success(function (data){
				//TODO: View related business logic
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

	//TODO: This function should be used to complete view related business logic for the edit referral form view
	$scope.prepareEditReferralForm = function (refId) {

	};

	//TODO: This method is entirely view related business logic, however it should be refactored to use the new referral service
	//Gather referral to be updated
	$scope.editReferral = function (refId) {
		//Get up to the minute referral information
		$http.get("/json/referral/" + refId).success(function ( data, status, headers) {
			$scope.referral = data.data;

			//TODO: View related business logic
			$scope.referral.lastEditedDate = that.getTodaysDate();
			//Append time to nextStepDate
			referral.nextStepDate += " " + referral.nextStepTime;
			//Set default status to OPEN
			for(var x = 0; x < $scope.refStatus.length; x++) {
				if($scope.referral.status == $scope.refStatus[x].status) {
					$scope.referral.status = $scope.refStatus[x];
				}
			}
			//Gather client information for editing as well
			$http.get("/json/client/" + $scope.referral.clientId).success(function ( data, status, headers) {
				$scope.referral.client = data.data;
			});
		});
	};

	//TODO: Handle view related edit referral view form submission
	$scope.editReferralFormSubmission = function (referral) {

	}

	//TODO: Refactor into the above function, using referral service
	//Have form submit to different method that can run the PUT instead of add (POST)
	$scope.updateReferral = function (referral) {
		$http.put("/json/referral", referral).success(function ( data, status, headers) {
			$http.put("/json/client", referral.client).success(function ( data, status, headers) {
				//TODO: View related redirection
				window.location.href = "/action/referral";
			});
		});
	};

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
	}
}]);