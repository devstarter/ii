function ItemRangeController($scope, $stateParams, $api) {

    $scope.from = $stateParams.from;
    $scope.to = $stateParams.to;
    document.title = $scope.from+"-"+$scope.to;
    $scope.loading = true;
    
    $api.item.getRange($scope.from, $scope.to)
        .then(function(items){
            $scope.loading = false;
            $scope.items = items;
        });
}

    