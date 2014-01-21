(function ($, undefined) {
    var searchApiUrl = ii.apiUrl + "search/", viewModel;
    ii.search = {
        load: function(query) {
            query = query.replace("+", " ");
            viewModel = kendo.observable({
                terms: [],
                contents: [],
                query: query,
                search: function(e) {
                    location.hash = "#search:"+viewModel.query.replace(" ", "+");
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