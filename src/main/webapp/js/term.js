(function ($, undefined) {
    var termApiUrl = ii.apiUrl + "term/";
    var viewModel;
    ii.term = {
        load: function(term) {
            document.title = term;
            viewModel = kendo.observable({
                loading: true,
                related: [],
                search: function(e) {
                    e.preventDefault();
                    ii.navigateToSearch(e.data.name)
                },
                navigate: function(e) {
                    ii.navigateToUri(e.data.uri);
                },
                save: function(e) {
                    console.trace("save");
                    $.post(termApiUrl, {
                        name: e.data.name,
                        description: e.data.description
                    }, function(e) {
                        location.reload();
                    })
                }
            });
            kendo.bind($('#term'), viewModel);
            $.ajax({
                url: termApiUrl,
                data: {name: term},
                success: function(r) {
                    for(var p in r) {
                        viewModel.set(p, r[p]);
                    }
                    viewModel.set("getLabel", function(data) {
                        return ii.labelByUri(data.uri);
                    });
                    viewModel.set("found", true);
                    viewModel.set("loading", false);

                    $.get(termApiUrl+"related", {uri: r.uri}, function(a) {
                        viewModel.set("related", a)
                    })
                },
                error: function(e) {
                    viewModel.set("name", term);
                    viewModel.set("found", false);
                    viewModel.set("loading", false);
                }
            })

        }
    }
})(jQuery);