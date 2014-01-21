var ii = {
    apiUrl: "api/",
    error: function (text) {
        noty({text: text, type: 'error', layout: 'topCenter', timeout: 1000});
    },
    navigateToUri: function(uri) {
        if (uri.indexOf("ии:пункт:") == 0) {
            var needReload = location.hash.indexOf("#item:") != 0;
            location.hash = "#item:"+uri.replace("ии:пункт:", "");
            if (needReload) location.reload();
        }
        if (uri.indexOf("ии:термин:") == 0) {
            var needReload = location.hash.indexOf("#term:") != 0;
            location.hash = "#term:"+uri.replace("ии:термин:", "").replace(" ", "+");
            if (needReload) location.reload();
        }
    },
    labelByUri: function(uri) {
        if (uri.indexOf("ии:пункт:") == 0) {
            return uri.replace("ии:пункт:", "");
        }
        if (uri.indexOf("ии:термин:") == 0) {
            return uri.replace("ии:термин:", "");
        }
    }
};

$(document).ajaxStart(function() {
    if (NProgress) NProgress.start();
});
$(document).ajaxStop(function() {
    if (NProgress) NProgress.done();
});
$.ajaxSetup({
    error: function (e, status, error) {
        if (e.status == 400) {
            ii.error("Ошибка запроса, указаны не все данные");
        }
        else if (e.status == 404) {
            ii.error("URL not found: "+this.url);
        }
        else {
            error = e.responseText.indexOf("<") == 0
                ? e.responseText
                : JSON.parse(e.responseText);
            if (error && error.error) {
                if (error.error.code != "UNDEFINED") {
                    ii.error(sf.labels.getLabel(error.error.code))
                } else {
                    ii.error(error.error.message)
                }
            } else {
                ii.error(e.responseText)
            }
        }
    }
});

var router = new kendo.Router();

router.route("item::item", function(item) {
    ensure({ html: "item.html", js: "js/item.js", parent: "content"}, function(){
        ii.item.load(item);
    });
});
router.route("term::term", function(term) {
    term = term.replace("+", " ");
    ensure({ html: "term.html", js: "js/term.js", parent: "content"}, function(){
        ii.term.load(term);
    });
});
router.route("search::query", function(query) {
    query = query.replace("+", " ");
    ensure({ html: "search.html", js: "js/search.js", parent: "content"}, function(){
        ii.search.load(query);
    });
});

$(document).ready(function() {
    router.start();

//    router.navigate("item\:1.0778");

    /*$.history.on('load change push', function(event, hash, type) {
        if (hash.indexOf("edit/")==0) {
            loadAdmin(hash.replace("edit/", ""));
            return
        }
        switch (hash) {
            case "play":
                loadGame();
                break;
            case "admin":
                loadAdmin(0);
                break;
            case "admin-goals":
                loadAdminGoals();
                break;
            default:
                loadGame();
                break;
        }
    }).listen('hash');*/
});