function HomeController($scope, $state) {
    $scope.search = function(query) {
        if (query) {
            $state.goToTerm(query);
        }
    };
}
