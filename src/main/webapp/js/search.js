(function ($, undefined) {
    var searchApiUrl = ii.apiUrl + "search/", viewModel;
    ii.search = {
        load: function(query) {
            document.title = "Поиск "+query;
            query = query.replace("+", " ");
            if (isItemNumber(query)) {
                location.hash = "#"+query;
                return
            }
            viewModel = kendo.observable({
                terms: [],
                contents: [],
                query: query,
                search: function(e) {
                    var q = typeof e == "string" ? e : viewModel.query;
//                    location.hash = "#search:"+q.replace(" ", "+");
                    ii.navigateToSearch(q);
                },
                getLabel: function(data) {
                    return ii.getLabel(data);
                },
                onkeypress: function(e) {
                    if(e.keyCode == 13)
                    {
                        viewModel.search(e.target.value);
                    }
                },
                navigate: function(e) {
                    ii.navigateToUri(e.data.uri);
                }
            });
            kendo.bind($('#search'), viewModel);
            if (query) search(query);
        }
    };
    var search = function(e) {
        $.get(searchApiUrl+"term", {
            query: viewModel.query
        }, function(r) {
            viewModel.set("loadingTerms", false);
            viewModel.set("terms", r);
            searchInContent();
        });
        viewModel.set("loadingTerms", true);
    };
    function searchInContent() {
        $.get(searchApiUrl+"content", {
            query: viewModel.query
        }, function(r) {
            viewModel.set("loadingContents", false);
            viewModel.set("contents", r)
        });
        viewModel.set("loadingContents", true);
    }
})(jQuery);