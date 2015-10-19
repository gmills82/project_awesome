//Fresh Referral Controller
app.controller('UpcomingApptsController', [
    "$scope",
    "$http",
    "ngTableParams",
    "$filter",
    'events',
    function ($scope, $http, ngTableParams, $filter, events) {
        $scope.referrals = [];

        this.init = function () {
            $scope.upcomingApptsTable = new ngTableParams({
                page: 1,            // show first page
                count: 10,
                sorting: {
                    nextStepTimestamp: 'asc'
                }
            }, {
                getData: function($defer, params) {
                    $http({"method": "GET", "url": "/json/appts/upcoming/" + app.data.currentUserId}).success(function (data){
                        //Filter
                        var filteredData = params.filter() ?
                            $filter('filter')(data.data, params.filter()) :
                            data.data;
                        //Then sort
                        var orderedData = params.sorting() ?
                            $filter('orderBy')(filteredData, params.orderBy()) :
                            filteredData;

                        orderedData = orderedData || [];

                        //Loop over appts and prepare combined address
                        angular.forEach(orderedData, function (value, key){
                            var address = "";
                            if(value.client && value.client.address1) {
                                address = value.client.address1;
                                if(value.client.city) {
                                    address = address + ", " + value.client.city;
                                }
                                if(value.client.state) {
                                    address = address + ", " + value.client.state;
                                }
                                if(value.client.zipcode) {
                                    address = address + ", " + value.client.zipcode;
                                }
                            }

                            value.client.address = address;
                        });

                        //Pass out total to larger scope
                        $scope.upcomingApptsTotal = orderedData.length;
                        params.total(orderedData.length);

                        //Resolve data gathering
                        $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
                    });
                }
            });

        };

        $scope.$on(events.REFERRAL_DELETED, function (event, args) {
            var referral = args.referral;
            for(var y = 0; y < $scope.upcomingApptsTable.data.length; y++) {
                if($scope.upcomingApptsTable.data[y].id === referral.id) {
                    $scope.upcomingApptsTable.data.splice(y, 1);
                }
            }
        });
    }
]);