function ItemController($scope, $stateParams, $log, $api) {
    $scope.number = $stateParams.number;
    $api.item.get($scope.number).then(function(html){
        $scope.html = html;
    })
}
