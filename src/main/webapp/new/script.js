var app = angular.module("MyApp", ["LiveSearch"]);
app.controller("MyController", function($scope, $http, $q, $window) {
    
    $scope.mySearch = "";
    
    $scope.mySearchCallback = function(params) {

      var defer = $q.defer();

      $http.get("http://ii.ayfaar.org/api/term/autocomplete?filter%5Bfilters%5D%5B0%5D%5Bvalue%5D=" + params.query)
        .then(function(response) {
          defer.resolve(response.data);
        })
        .catch(function(e) {
          $window.alert(e.message);
          defer.reject(e);
        });

        return defer.promise;
    };
});
