function CategoryController($scope, $stateParams, $api, $state) {

    $scope.name = $stateParams.name;
    document.title = $scope.name;
    $scope.loading = true;

    $api.category.get($scope.name).then(function(category){
        $scope.loading = false;
        copyObjectTo(category, $scope);
        document.title = $scope.name;
    });
}

    