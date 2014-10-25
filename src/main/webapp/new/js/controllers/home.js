function HomeController($scope, $api, $log, $state) {
    /*$scope.getSuggestions = function(query) {
        return $api.get("v2/suggestions/"+query)
    };
    $scope.suggestionSelected = function(suggestion) {
        $state.go("term", {name: suggestion});
    };*/
    $scope.search = function(query) {
        if (query) {
            $state.go("term", {name: query});
        }
    };
}
