var ii = {
    apiUrl: "api/",
    error: function (text) {
        noty({text: text, type: 'error', layout: 'topCenter', timeout: 1000});
    },
    navigateToSearch: function(query) {
        var needReload = location.hash.indexOf("#?") != 0;
        location.hash = "#?"+query.replaceAll(" ", "+");
        if (needReload) location.reload();
    },
    navigateToUri: function(uri) {
        if (uri.indexOf("ии:пункт:") == 0) {
            var needReload = !isItemNumber(location.hash.replace("#", ""));//.indexOf("#item:") != 0;
            location.hash = "#"+uri.replace("ии:пункт:", "");
            if (needReload) location.reload();
        }
        if (uri.indexOf("ии:термин:") == 0) {
            var needReload = isItemNumber(location.hash.replace("#", "")) || location.hash.indexOf("#?") == 0;//location.hash.indexOf("#term:") != 0;
            location.hash = "#"+uri.replace("ии:термин:", "").replaceAll(" ", "+");
            if (needReload) location.reload();
        }
        if (uri.indexOf("статья:") == 0) {
            var needReload = location.hash.indexOf("#a/") != 0;
            location.hash = "#a/"+uri.replace("статья:", "");
            if (needReload) location.reload();
        }
    },
    getLabel: function(d) {
        if (d.uri.indexOf("ии:пункт:") == 0) {
            return d.uri.replace("ии:пункт:", "");
        }
        if (d.uri.indexOf("ии:термин:") == 0) {
            return d.uri.replace("ии:термин:", "");
        }
        if (d.uri.indexOf("статья:") == 0) {
            return "Статья «"+d.name+"»";
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

function isItemNumber(s) {
    return s.match("\\d+\\.\\d+");
}

String.prototype.replaceAll = function(target, replacement) {
    return this.split(target).join(replacement);
};

var router = new kendo.Router();

router.route("item::item", itemRoute);
function itemRoute(item) {
    ensure({ html: "item.html", js: "js/item.js", parent: "content"}, function(){
        ii.item.load(item);
    });
}
router.route("term::term", termRoute);
function termRoute(term) {
    term = term.replaceAll("+", " ");
    ensure({ html: "term.html", js: "js/term.js", parent: "content"}, function(){
        ii.term.load(term);
    });
}
router.route("search::query", searchRoute);
router.route("?:query", searchRoute);
function searchRoute(query) {
    query = query.replaceAll("+", " ");
    ensure({ html: "search.html", js: "js/search.js", parent: "content"}, function(){
        ii.search.load(query);
    });
}
router.route("main", function() {
    location.reload();
});
router.route("a/:id", function(id) {
    ensure({ html: "article.html", js: "js/article.js", parent: "content"}, function(){
        ii.article.load(id);
    });
});
router.route("about", function() {
    ensure({ html: "about.html", parent: "content"});
});
router.route(":hash", function(hash) {
    if (isItemNumber(hash)) {
        itemRoute(hash)
    } else {
        termRoute(hash)
    }
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