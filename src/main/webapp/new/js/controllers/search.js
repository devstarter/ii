function SearchController($scope, $stateParams, $api, $state) {
    var pageCounter = 0, currentQuery;

    var query = $scope.query = $stateParams.query;

    if (currentQuery == query) {
        return;
    }
    currentQuery = query;
    pageCounter = 0;
    document.title = "Поиск " + query;
    query = query.replace("+", " ").trim();
    if (isItemNumber(query)) {
        $state.goToItem(query);
        return
    }

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
    $scope.rateUp = function (quote) {
        $api.post("search/rate/+", {
            uri: "ии:пункт:" + quote.number,
            quote: quote.quote,
            query: $scope.query
        }).then(rateComplete);
    };
    $scope.expand = function(quote) {
        $api.get("search/get-content", {uri: "ии:пункт:"+quote.number})
            .then(function(r){
                quote.full = r;
            })
    };
    if (query) search(query);
    /*$("#search").find(".prompt").keypress(function (e) {
     if (e.which == 13) {
     $scope.search($scope.query);
     }
     pageCounter = 0;
     });*/

    function search(e) {
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
    }

    function searchInContent() {
        $api.get("v2/search", {
            query: $scope.query,
            pageNumber: pageCounter
        }).then(function (r) {
            pageCounter++;
            $scope.quotes = $scope.quotes ? $scope.quotes.concat(r.quotes) : r.quotes;
            if ($scope.quotes.length == 0) {
                // no result
                $scope.noResult = $scope.quotes.length == 0;
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
        if (!$scope.quotes || !$scope.quotes.length) {
            $scope.loadingContents = true;
        } else {
            $scope.loadingMore = true;
        }

    }

    function rateComplete() {
        alert("Ваш голос учтён, благодарим за помощь! :)")
    }
}