function CategoryController($scope, $stateParams, $api) {
    $scope.name = $stateParams.name;
    $api.category.get($scope.name).then(function(caterory){
        copyObjectTo(caterory, $scope);
    })
}
