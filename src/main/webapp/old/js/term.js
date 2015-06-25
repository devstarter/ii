﻿(function ($, undefined) {
    var termApiUrl = ii.apiUrl + "term/";
    var viewModel;
    ii.term = {
        load: function(term) {
            document.title = term;
            viewModel = kendo.observable({
                loading: true,
                related: [],
                aliases: [],
                quotes: [],
                getLabel: function(data) {
                    return ii.getLabel(data);
                },
                search: function(e) {
                    e.preventDefault();
                    ii.navigateToSearch(e.data.name)
                },
                navigate: function(e) {
                    ii.navigateToUri(e.data.uri);
                },
                navigateToCode: function(e) {
                    ii.navigateToUri(e.data.code.uri);
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
                        return ii.getLabel(data);
                    });
                    viewModel.set("found", true);
                    viewModel.set("loading", false);

                    /*$.get(termApiUrl+"related", {uri: r.uri}, function(a) {
                        viewModel.set("related", a)
                    })*/
                    if (ga && !r.description && !r.shortDescription && !r.quotes.length) {
                        ga('send', 'event', 'no-data', term);
                    }
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