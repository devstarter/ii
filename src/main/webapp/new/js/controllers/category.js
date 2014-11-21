function CategoryController($scope, $stateParams, $api, $state) {

    $scope.name = $stateParams.name;
    // запрос на получение категории
    $scope.loading = true;
    
    $api.category.get($scope.name).then(function(caterory){
        // категория получена, копируем её поля в скоп для доступа к ним в category.html
        $scope.loading = false;    
        copyObjectTo(caterory, $scope);
        //console.log($scope);
    });
    
    $scope.navigate = function(entity) {
        //$state.go(entity);
    };
}

    