(function () {
    var app = angular.module("project_awesome", ["ngRoute"]);
    app.data = {};
	if(document.getElementById("baseContainer")){
		app.data.currentUserId = document.getElementById("baseContainer").getAttribute("data-user");
	}

    //Actions controller
    app.controller('ActionController', ["$scope", "$http", "$attrs", function ($scope, $http, $attrs){
        $scope.actions = [];
        $http({"method": "GET", "url": "/actions/" + app.data.currentUserId + "/" + $attrs.category}).success(function (data){
            $scope.actions = data.data;
        });
    }]);

    //Profile controller
    app.controller('ProfileController', ["$scope", "$http", function ($scope, $http) {
        $scope.profile = {};
        $scope.profile.client = {};
		$scope.prefillFromReferral = function (refId) {
			//Get Referral
			$http.get("/json/referral/" + refId).success(function (data, status, headers) {
				var referral = data.data;
				//Get Client
				$http.get("/json/client/" + referral.clientId).success(function (data, status, headers) {
					var client = data.data;
					//Prefill notes
					client.refNotes = referral.refNotes;

					//Prefill client info
					$scope.profile.client = client;
					$scope.$$childHead.client = client;
					$scope.$$childHead.mode = "edit";
				}).error(function (error) {
					console.log("Client could not be retrieved from the referral with id " + refId);
				})
			}).error(function (error) {
				console.log("Referral with id " + refId + " could not be retrieved.");
			});
		}

		if(window.location.href.match(/\?refId=.*$/)){
			var param = window.location.href.match(/\?refId=.*$/)[0];
			param = param.slice(param.indexOf("=") + 1);
			$scope.prefillFromReferral(param);
		}

        $scope.addProfile = function (profile) {
            $http.post("/json/profile", profile).success(function (data, status, headers){
				var profileId = headers("Location").match(/\/\d*$/);
				if(profileId.length) {
					profileId = profileId[0];
					profileId = profileId.slice(1);
					window.location="/action/profileReview/" + profileId;
				}else {
					console.log(profileId, "Profile id was not pulled from the Location header");
				}

            }).error(function(jqxhr, status, error){
				console.log("Could not save profile. Server responsded with " + error);
			});
        };
    }]);

    //Client controller
    app.controller('ClientController', ["$scope", "$http", function ($scope, $http) {
        $scope.client = {};
		$scope.client.goals = [];
        $scope.mode = "add";
        $scope.prepareProfileScope = function (client) {
            $scope.$parent.profile.client = client;
            $scope.$parent.profile.clientId = client.id;
            $scope.$parent.profile.agentId = app.data.currentUserId;
            $scope.mode = "edit";
            //Show client information display tab pane
            $('#clientTab a[href=#display]').tab('show');
        };
        $scope.addClient = function (client) {
            var failFunc = function(jqxhr, status, error) {
                console.log("Client was unable to be updated. Server responded with " + status + " error: " + error);
            }
            if($scope.mode === "add") {
                $http.post("/json/client", client).success(function (data, status, headers) {
                    //Lookup new resource with a GET
                    $http.get(headers("Location")).success(function (data){
                        $scope.prepareProfileScope(data.data);
                        $scope.client = data.data;
                    });
                }).error(failFunc);
            }else if ($scope.mode === 'edit') {
                $http.put("/json/client", client).success(function (data, status, headers) {
                    console.log("Successfully updated client: " + client.name);
                    //Lookup new resource with a GET
                    $http.get(headers("Location")).success(function (data){
                        $scope.prepareProfileScope(data.data);
                        $scope.client = data.data;
                    });
                }).error(failFunc);
            }
        };

        $scope.preparePanel = function () {
            //If add mode then land panel on the display tab
            if($scope.mode === "add") {
                $('#clientTab a[href=#display]').show();
            }
        };

        $scope.prepareAddClientPanel = function () {
            //Button functionality
            $('.btn').click(function () {
                $(this).siblings().removeClass('active');
                $(this).addClass('active');
            });

            //Check active buttons on tab show
            $('a[href=#client]').on("shown", function () {
                $('input[type="radio"]').each(function (index, ele) {
                    if($(ele).parents("label").hasClass('active')) {
                        $(ele).click();
                        $scope.$apply()
                    }
                });
            });
        };
    }]);

    //Assets controller
    app.controller('AssetController', ["$scope", "$http", function ($scope, $http) {
        $scope.current = {};
        $scope.assetTypes = [];
        $http({"method": "GET", "url": "/json/assetTypes"}).success(function (data){
            $scope.assetTypes = data.data;
        });
        $scope.addAsset = function (asset) {
            //Get current client
            $http.get("/json/client/" + $scope.$parent.profile.client.id).success(function (data, status, headers){
                //Add asset to client
                var client = data.data;
                client.assetList.push(asset);
				$scope.current = {};

                //Call client update which calls profile update
                $scope.$parent.$$childHead.addClient(client);
            }).error(function (xhr, status, err) {
                console.log("Asset unable to be added: " + err);
            });
        }
    }]);

    //Debt controller
    app.controller('DebtController', ["$scope", "$http", function ($scope, $http) {
		$scope.current = {};
		$scope.debtTypes = [];
		$http({"method": "GET", "url": "/json/debtTypes"}).success(function (data){
			$scope.debtTypes = data.data;
		});
		$scope.adddebt = function (debt) {
			//Get current client
			$http.get("/json/client/" + $scope.$parent.profile.client.id).success(function (data, status, headers){
				//Add debt to client
				var client = data.data;
				client.debtList.push(debt);
				$scope.current = {};

				//Call client update which calls profile update
				$scope.$parent.$$childHead.addClient(client);
			}).error(function (xhr, status, err) {
				console.log("Debt unable to be added: " + err);
			});
		}
    }]);

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
    }]);
})();