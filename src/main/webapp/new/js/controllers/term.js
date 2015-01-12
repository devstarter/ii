function TermController($scope, $stateParams, $api, $state, analytics, $modal) {
    var pageCounter = 0, currentQuery;
    var query = $scope.query = $stateParams.name;
    if (!query) {
        $state.goToHome();
        return
    }
    if (isItemNumber(query)) {
        $state.goToItem(query);
        return
    }
    query = query.replace("+", " ").trim();
    $scope.name = query;
    document.title = query;
    $scope.state = $state;

    $scope.loading = true;

    $api.term.get(query).then(function (data) {
        $scope.termFound = true;
        copyObjectTo(data, $scope);
        if (data && !data.description && !data.shortDescription && !data.quotes.length && !data.related.length) {
            analytics.registerEmptyTerm(data.name);
        }
        if (data && !data.description && !data.shortDescription && !data.quotes.length) {
            $scope.search();
        }
    }, function () {
        $scope.search();
    })
    ['finally'](function () {
        $scope.loading = false;
    });

    $scope.searchCallback = function() {
        return $api.search.suggestions($scope.name)
    };
    $scope.suggestionSelected = function(suggestion) {
        $state.goToTerm(suggestion);
    };

    $scope.loadNextPage = function () {
        searchInContent();
    };
    $scope.rateUp = function (item) {
        if(getSelectionText()) {
            var modalInstance = $modal.open({
                templateUrl: 'search-contribute-form.html',
                controller: function ($scope, $modalInstance) {
                    $scope.text = getSelectionText();
                    $scope.term = query;
                    $scope.quote = function() {
                        var data = {
                            uri: "ии:пункт:" + item.number,
                            query: $scope.term
                        };
                        var quote = getSelectionText() || (item.full ? null : item.quote);
                        if (quote) data.quote = quote;
                        $api.post("search/rate/+", data).then(function(){
                            $modalInstance.close();
                            rateComplete();
                        });
                    };
                    $scope.link = function(type) {
                        if (!$scope.term || !$scope.text) return;
                        return $api.term.link($scope.term, $scope.text, type).then(function(){
                            $modalInstance.close();
                        });
                    }
                }
            });
            return;
        }

        if (confirm("Цитата будет помечена как помогающая понять этот термин, вы согласны?")) {
            var data = {
                uri: "ии:пункт:" + item.number,
                query: $scope.query
            };
            var quote = getSelectionText() || (item.full ? null : item.quote);
            if (quote) {
                data.quote = quote;
            }
            $api.post("search/rate/+", data).then(rateComplete);
        }
    };
    $scope.expand = function(quote) {
        quote.loadingFull = true;
        $api.item.getContent(quote.uri ? quote.uri : quote.number)
            .then(function(r){
                quote.full = r;
            })
            ['finally'](function () {
                quote.loadingFull = false;
            });
    };

    $scope.navigate = function(entity) {
        $state.go(entity);
    };

    $scope.search = function() {
        if (currentQuery == query) {
            return;
        }
        currentQuery = query;
        pageCounter = 0;
        if (!$scope.termFound) {
            $api.search.term($scope.query).then(function (r) {
                $scope.loadingTerms = false;
                $scope.terms = r.terms;
                $scope.articles = r.articles;
                $scope.exactMatchTerm = r.exactMatchTerm;
                searchInContent();
            });
            $scope.loadingTerms = true;
        } else {
            searchInContent();
        }
    };

    function searchInContent() {
        $api.search.content($scope.query, pageCounter).then(function (r) {
            pageCounter++;
            $scope.foundQuotes = $scope.foundQuotes ? $scope.foundQuotes.concat(r.quotes) : r.quotes;
            if ($scope.foundQuotes.length == 0) {
                // no result
                $scope.noResult = $scope.foundQuotes.length == 0;
                if (ga) ga('send', 'event', 'not-found', $scope.query);
            }
            $scope.showLoadMore = r.hasMore;
            if (!r.quotes.length) {
                pageCounter = 0;
            }
        })['finally'](function(){
            $scope.loadingContents = false;
            $scope.loadingMore = false;
        });
        if (!$scope.foundQuotes || !$scope.foundQuotes.length) {
            $scope.loadingContents = true;
        } else {
            $scope.loadingMore = true;
        }

    }

    function rateComplete() {
        alert("Ваш голос учтён, благодарим за помощь! :)")
    }
}
