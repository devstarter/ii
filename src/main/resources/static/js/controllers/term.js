function TermController($scope, $stateParams, $api, $state, statistic, $modal, modal) {
    var pageCounter = 0, currentQuery;
    var query = $scope.query = $stateParams.name;
    if (query == "{{url}}") return; // strange bug
    if (!query) {
        $state.goToHome();
        return
    }
    if (isItemNumber(query)) {
        $state.goToItem(query);
        return
    }
    if (isItemRange(query)) {
        var items = query.split("-");
        $state.goToItemRange(items[0], items[1]);
        return
    }
    query = query.replace("+", " ").replace("_", " ").replace("Обсуждение:", "").trim();
    $scope.name = query;
    document.title = query;
    $scope.state = $state;

    $scope.loading = true;

    $api.term.get(query).then(function (data) {
        if (!data) return noTermHandler();
        
        $scope.termFound = true;
        copyObjectTo(data, $scope);
        if (data && !data.description && !data.shortDescription && !data.quotes.length && !data.related.length) {
            statistic.registerEmptyTerm(data.name);
        }
        if (data && !data.description && !data.shortDescription && !data.quotes.length && data.categories.length) {
            $scope.showCategories = true;
        }
        if (data && !data.description && !data.shortDescription && !data.quotes.length && !data.categories.length) {
            $scope.search();
        }

        var metaDescription = (data.shortDescription ? data.shortDescription+"\n" : "")
            +(data.description ? data.description : "");
        if ($scope.$root) $scope.$root.metaDescription = metaDescription.trim();

        var keywords = '';
        for(i in data.related) {
            keywords += ","+ data.related[i].name;
        }
        $scope.$root.metaKeywords = keywords;
        $scope.showQuotes = data.quotes.length < 3;
        $scope.showCategories = data.categories.length < 3;
        //$scope.$root.hideLoop = false;
    }, noTermHandler)
    ['finally'](function () {
        $scope.loading = false;
    });

    function noTermHandler() {
        $scope.editMode = true;
        //$scope.$root.hideLoop = true;
        $scope.search();
    }

    $scope.searchCallback = function() {
        return $api.search.suggestions($scope.name)
    };
    $scope.suggestionSelected = function(suggestion) {
        $state.goToTerm(suggestion);
    };

    $scope.loadNextPage = function () {
        searchInContent();
        statistic.searchNextPageLoading(pageCounter, query)
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

        if (confirm("Вы можете принять участие в полезном наполнении этого сайта. Добавить в список избранных цитат для этого термина?")) {
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

    $scope.navigate = function(termName) {
        window.open(termName, '_blank');
        //$state.go(entity);
    };

    $scope.search = function(newQuery) {
        if (newQuery) {
            $state.goToTerm(newQuery);
            return;
        }
        if (currentQuery == query) {
            return;
        }
        currentQuery = query;
        pageCounter = 0;
        if ($scope.termFound) {
            searchInContent();
        } else {
            $api.search.term($scope.query).then(function (r) {
                $scope.loadingTerms = false;
                $scope.terms = r.terms;
                $scope.articles = r.articles;
                $scope.exactMatchTerm = r.exactMatchTerm;
                $scope.categories = r.categories;
                $scope.showSCategories = r.categories.length < 3;
                if (!r.categories.length) searchInContent();
            });
            $scope.loadingTerms = true;
        }
    };

    function searchInContent() {
        $api.search.content($scope.query, $scope.startSearchFrom, pageCounter).then(function (r) {
            pageCounter++;
            for(var i in r.quotes) {
                r.quotes[i].uri = "ии:пункт:"+r.quotes[i].number;
            }
            $scope.foundQuotes = $scope.foundQuotes ? $scope.foundQuotes.concat(r.quotes) : (r.quotes ? r.quotes : []);
            if ($scope.foundQuotes.length == 0) {
                // no result
                $scope.noResult = $scope.foundQuotes.length == 0;
                if (ga) ga('send', 'event', 'not-found', $scope.query);
            }
            $scope.showLoadMore = r.has_more;
            if (r.quotes && !r.quotes.length) {
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
    $scope.searchInContent = searchInContent;

    function rateComplete() {
        alert("Ваш голос учтён, благодарим за помощь! :)")
    }

    $scope.advancedSearch = function() {
        $scope.foundQuotes = [];
        $scope.loadingContents = true;
        pageCounter = 0;
        searchInContent();
    };

    $scope.rename = function () {
        modal.prompt("Переименование термина", $scope.name, "Переименовать").then(function (newName) {
            $api.term.rename($scope.name, newName).then(function () {
                $state.goToTerm(newName)
            })
        })
    }
}
