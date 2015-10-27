app.factory('userService', ['$http', '$log', function($http, $log) {

    return {
        getById: function (id, callback) {
            $http.get("/json/user/" + id).success(function (data) {
                if (typeof callback === "function") {
                    callback(data);
                }
            });
        }
    }

}]);