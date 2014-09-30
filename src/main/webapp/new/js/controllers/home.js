function HomeController($scope, $api, $log, $state) {
    $scope.searchCallback = function() {
        return $api.get("v2/suggestions/"+$scope.query)
    };
    $scope.suggestionSelected = function(suggestion) {
        $state.go("term", {name: suggestion});
    };
    $scope.search = function() {
        if ($scope.query) {
            $state.go("term", {query: $scope.query});
        }
    };
}
