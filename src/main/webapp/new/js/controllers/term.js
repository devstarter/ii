function TermController($scope, $stateParams, $api, analytics) {
    $scope.name = $stateParams.name;
    $scope.loading = true;
    $api.get('term/', {name: $stateParams.name})
        .then(function(data) {
            $scope.found = true;
            for(var p in data) {
                $scope[p] = data[p];
            }
            if (data && !data.description && !data.shortDescription && !data.quotes.length && !data.related.length) {
                analytics.registerEmptyTerm(data.name);
            }
        })
        ['finally'](function(){
            $scope.loading = false;
        })
}
