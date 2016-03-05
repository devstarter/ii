(function ($, undefined) {
    var searchApiUrl = ii.apiUrl + "search/", viewModel, pageCounter = 0, currentQuery;
    ii.search = {
        load: function(query) {
            if (currentQuery == query) {
                return;
            }
            currentQuery = query;
            pageCounter = 0;
            document.title = "Поиск "+query;
            query = query.replace("+", " ").trim();
            if (isItemNumber(query)) {
                location.hash = "#"+query;
                return
            }
            viewModel = kendo.observable({
                terms: [],
                contents: [],
                query: query,
                loadNextPageLabel: "Искать далее...",
                search: function(e) {
                    var q = typeof e == "string" ? e : viewModel.query;
//                    location.hash = "#search:"+q.replace(" ", "+");
                    ii.navigateToSearch(q);
                },
                getLabel: function(data) {
                    return ii.getLabel(data);
                },
                navigate: function(e) {
                    ii.navigateToUri(e.data.uri);
                },
                navigateToExactTerm: function(e) {
                    ii.navigateToUri(e.data.exactMatchTerm.uri);
                },
                loadNextPage: function(e) {
                    searchInContent();
                },
                rateUp: function(e) {
                    $.post(searchApiUrl+"rate/+", {uri: e.data.uri, quote: e.data.quote, query: viewModel.query}, rateComplete);
                },
                getContent: function(e) {
                    /*$.get(searchApiUrl+"get-content", {uri: e.data.uri}, function(r) {
                        if (r) {
                            for(var i in viewModel.contents) {
                                var quote = viewModel.contents[i];
                                if (quote.uri == e.data.uri) {
                                    quote.set("quote", r);
                                    break
                                }
                            }
                        }
                    })*/
                }
            });
            kendo.bind($('#search'), viewModel);
            if (query) search(query);
            $("#search").find(".prompt").keypress(function (e) {
                if (e.which == 13) {
                    viewModel.search(viewModel.query);
                }
                pageCounter = 0;
            });
        }
    };
    var search = function(e) {
        $.get(searchApiUrl+"term", {
            query: viewModel.query
        }, function(r) {
            viewModel.set("loadingTerms", false);
            viewModel.set("terms", r.terms);
            viewModel.set("exactMatchTerm", r.exactMatchTerm);
            searchInContent();
        });
        viewModel.set("loadingTerms", true);
    };
    function searchInContent() {
        $.get(searchApiUrl+"content", {
            query: viewModel.query,
            page: pageCounter
        }, function(r) {
            pageCounter++;
            viewModel.set("loadingContents", false);
            viewModel.set("contents", viewModel.contents.toJSON().concat(r));
            if (viewModel.contents.length == 0) {
                // no result
                viewModel.set("noResult", viewModel.contents.length == 0);
                if (ga) ga('send', 'event', 'not-found', viewModel.query);
            }
            viewModel.set("showLoadMore", r.length > 0);
            if (!r.length) {
                pageCounter = 0;
            }
        });
        if (viewModel.contents.length == 0) {
            viewModel.set("loadingContents", true);
        }
    }
    function rateComplete() {
        alert("Ваш голос учтён, благодарим за помощь! :)")
    }
})(jQuery);