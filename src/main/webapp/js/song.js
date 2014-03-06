(function ($, undefined) {
    var songApiUrl, viewModel;
    ii.song = {
        load: function(id) {
            songApiUrl = ii.apiUrl + "song/"+id+"/";
            viewModel = kendo.observable({
                loading: true,
				navigateNext: function(e) {
                    ii.navigateToUri(e.data.next.uri);
                },
				navigatePrev: function(e) {
                    ii.navigateToUri(e.data.prev.uri);
                }
            });
            kendo.bind($('#song'), viewModel);
            $.get(songApiUrl, setData);

            function setData(r) {
				window.scrollTo(0, 0);
                //document.title = "Песни MZ2";
                for(var p in r) {
                    viewModel.set(p, r[p]);
                }
				viewModel.set("loading", false);
            }
        }
    };
})(jQuery);