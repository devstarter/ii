function ItemController($scope, $stateParams, $state, $api) {
    $scope.number = $stateParams.number;
    if (!$scope.number) {
        $state.goToHome();
        return
    }
    document.title = $scope.number;

    $scope.content = "Загрузка...";
    $api.item.get($scope.number).then(function (item) {
        copyObjectTo(item, $scope);
    });
    $scope.goPrev = function() {
        $state.go($scope.previous);
    };
    $scope.goNext = function() {
        $state.go($scope.next);
    }
}
