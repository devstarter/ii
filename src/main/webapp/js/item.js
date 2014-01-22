(function ($, undefined) {
    var itemApiUrl, viewModel;
    ii.item = {
        load: function(item) {
            document.title = "ИИ "+item;
            itemApiUrl = ii.apiUrl + "item/";

            viewModel = kendo.observable({
                loading: true,
                next: {},
                linkedTerms: [],
                navigateToTerm: function(e) {
                    ii.navigateToUri(e.data.uri);
                },
                navigateNext: function(e) {
                    ii.navigateToUri(e.data.next.uri);
                }
            });
            kendo.bind($('#item'), viewModel);

            $.get(itemApiUrl+item+"/", function(r) {
                for(var p in r) {
                    viewModel.set(p, r[p]);
                }
                viewModel.set("loading", false);
                viewModel.set("nextButtonLabel", function(d) {
                    return d.next ? d.next.number + " →" : "";
                });

                $.get(itemApiUrl+item+"/linked-terms", function(a) {
                    viewModel.set("linkedTerms", a)
                })
            })
        }
    }
})(jQuery);