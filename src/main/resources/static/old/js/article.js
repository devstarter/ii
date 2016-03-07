(function ($, undefined) {
    var articleApiUrl, viewModel;
    ii.article = {
        load: function(id) {
            articleApiUrl = ii.apiUrl + "article/"+id+"/";

            viewModel = kendo.observable({
                loading: true,
                relatedTerms: [],
                navigateToTerm: function(e) {
                    ii.navigateToUri(e.data.uri);
                }
            });
            kendo.bind($('#article'), viewModel);

            $.get(articleApiUrl, setData);

            function setData(r) {
                document.title = "Статья "+ r.name;
                for(var p in r) {
                    viewModel.set(p, r[p]);
                }
                viewModel.set("loading", false);

                $.get(articleApiUrl+"/related-terms", function(a) {
                    viewModel.set("relatedTerms", a);
                })
            }
        }
    };
})(jQuery);