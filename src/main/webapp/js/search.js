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
            viewModel.set("terms", r.termList);
            searchInContent();
        });
        viewModel.set("loadingTerms", true);
    };
    function searchInContent() {
        $.get(searchApiUrl+"content", {
            query: viewModel.query
        }, function(r) {
            viewModel.set("loadingContents", false);
            viewModel.set("getNumber", function(d){
                return d.number;
            });
            viewModel.set("getPart", function(d){
                return d.part;
            });
            viewModel.set("contents", r)
        });
        viewModel.set("loadingContents", true);
    }
})(jQuery);