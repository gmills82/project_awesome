//Client controller
app.controller('ClientController', [
    "$scope",
    "$http",
    "referralService",
    "clientService",
    "events",
    function ($scope, $http, referralService, clientService, events) {
        $scope.client = {};
        $scope.client.goals = [];

        /**
         * Returns formated date string of 18 years ago from today
         * @returns {string}
         */
        this.getOfAgeDate = function() {
            var date = new Date();
            date.setFullYear(date.getFullYear() - 18);
            var dateString = date.getFullYear() + "-";
            var month = date.getMonth() + 1;
            if (month.toString().length < 2) {
                month = "0" + month;
            }
            var day = date.getDate() + 1;
            if (day.toString().length < 2) {
                day = "0" + day;
            }
            dateString += month + "-" + day;

            return dateString;
        };

        $scope.maxDate = this.getOfAgeDate();

        /**
         * Used to pass clients to the client search view
         * @param query
         */
        this.queryClients = function (query) {
            $http.get('/json/client/query/' + query).success(function (data, status, headers) {
                $scope.clients = data.data;
            });
        };

    }
]);