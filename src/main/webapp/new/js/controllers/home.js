function HomeController($scope, $state) {
    $scope.search = function(query) {
        if (query) {
            $state.go("term", {name: query});
        }
    };
}
