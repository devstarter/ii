function TermController($scope, $stateParams, $api, $state, analytics) {
    var pageCounter = 0, currentQuery;
    var query = $scope.query = $stateParams.name;
    if (isItemNumber(query)) {
        $state.goToItem(query);
        return
    }
    query = query.replace("+", " ").trim();
    $scope.name = query;
    loadTerm();

    function loadTerm() {
        $scope.loading = true;
        $api.get('term/', {name: $stateParams.name}, true)
            .then(function (data) {
                $scope.termFound = true;
                for (var p in data) {
                    $scope[p] = data[p];
                }
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
        document.title = query;
    }

    $scope.searchCallback = function() {
        return $api.get("v2/suggestions/"+$scope.name)
    };
    $scope.suggestionSelected = function(suggestion) {
        $state.go("term", {name: suggestion});
    };

    $scope.updateName = function() {
        $state.go("term", {name: $scope.name});
    };

    /*search: function(e) {
     var q = typeof e == "string" ? e : $scope.query;
     ii.navigateToSearch(q);
     },*/

    /* navigateToExactTerm: function(e) {
     ii.navigateToUri(e.data.exactMatchTerm.uri);
     },*/
    $scope.loadNextPage = function () {
        searchInContent();
    };
    $scope.rateUp = function (item) {
        var data = {
            uri: "ии:пункт:" + item.number,
            query: $scope.query
        };
        var quote = getSelectionText() || (item.full ? null : item.quote);
        if (quote) {
            data.quote = quote;
        }
        $api.post("search/rate/+", data).then(rateComplete);
    };
    $scope.expand = function(quote) {
        var uri = quote.uri ? quote.uri : "ии:пункт:"+quote.number;
        $api.get("search/get-content", {uri: uri})
            .then(function(r){
                quote.full = r;
            })
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
            $api.get("search/term", {
                query: $scope.query
            }).then(function (r) {
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
        $api.get("v2/search", {
            query: $scope.query,
            pageNumber: pageCounter
        }).then(function (r) {
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

    function getSelectionText() {
        var text = "";
        if (window.getSelection) {
            text = window.getSelection().toString();
        } else if (document.selection && document.selection.type != "Control") {
            text = document.selection.createRange().text;
        }
        return text;
    }
}
