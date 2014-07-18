(function ($, undefined) {
    var itemApiUrl, viewModel, preloadedNext;
    ii.item = {
        load: function(item) {
            if (!viewModel) {
                // init
                $("#item").touchwipe({
                    wipeLeft: function(){
                        if (viewModel.next) ii.navigateToUri(viewModel.next.uri);
                    },
                    wipeRight: function(){
                        if (viewModel.prev) ii.navigateToUri(viewModel.prev.uri);
                    },
//                wipeUp: function() { alert("up"); },
//                wipeDown: function() { alert("down"); },
                    min_move_x: 20,
                    min_move_y: 20,
                    preventDefaultEvents: false
                });
            }
            document.title = "ИИ "+item;
            itemApiUrl = ii.apiUrl + "item/";

            viewModel = kendo.observable({
                loading: true,
                next: {},
                linkedTerms: [],
                backLabel: function() {
                    return ii.prevScreen == "search" ? "Назад к результам поиска" : null
                },
                goBack: function() {
                    window.browser.back();
                },
                navigateToTerm: function(e) {
                    ii.navigateToUri(e.data.uri);
                },
                navigateNext: function(e) {
                    ii.navigateToUri(e.data.next.uri);
                },
                navigatePrev: function(e) {
                    ii.navigateToUri(e.data.prev.uri);
                }
            });
            kendo.bind($('#item'), viewModel);

            if (preloadedNext && preloadedNext.number == item) {
                setData(preloadedNext);
                loadNext();
            } else {
                $.get(itemApiUrl+item+"/", setData)
            }

            function setData(r) {
                window.scrollTo(0, 0);
                for(var p in r) {
                    viewModel.set(p, r[p]);
                }
                viewModel.content = viewModel.content.replaceAll("\n", "<br/>");
                viewModel.set("loading", false);
                viewModel.set("nextButtonLabel", function(d) {
                    return d.next ? d.next.number + " →" : "";
                });

                $.get(itemApiUrl+item+"/linked-terms", function(a) {
                    viewModel.set("linkedTerms", a);
                    loadNext();
                })
            }
        }
    };

    function loadNext() {
        preloadedNext = false;
        if (viewModel.next) {
            $.get(itemApiUrl+viewModel.next.number+"/", function (r) {
                preloadedNext = r
            })
        }
    }
})(jQuery);