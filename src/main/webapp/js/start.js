var ii = {
    apiUrl: "api/",
    error: function (text) {
        noty({text: text, type: 'error', layout: 'topCenter', timeout: 3000});
    },
    navigateToSearch: function(query) {
        setHash("?"+query.replaceAll(" ", "+"));
    },
    navigateToUri: function(uri) {
        if (uri.indexOf("ии:пункт:") == 0) {
            setHash(uri.replace("ии:пункт:", ""));
        }
        if (uri.indexOf("ии:термин:") == 0) {
            setHash(uri.replace("ии:термин:", "").replaceAll(" ", "+"));
        }
        if (uri.indexOf("статья:") == 0) {
            setHash("a/"+uri.replace("статья:", ""));
        }
		if (uri.indexOf("песня:") == 0) {
            setHash("s/"+uri.replace("песня:", ""));
        }
        if (uri.indexOf("http") == 0) {
            window.open(uri,'_blank');
        }
    },
    getLabel: function(d) {
        var uri = d.uri;
        if (uri.indexOf("ии:пункт:") == 0) {
            return uri.replace("ии:пункт:", "");
        }
        if (uri.indexOf("ии:термин:") == 0) {
            return uri.replace("ии:термин:", "");
        }
        if (uri.indexOf("статья:") == 0) {
            return "Статья «"+d.name+"»";
        }
		if (uri.indexOf("песня:") == 0) {
            return "Песня «"+d.name+"»";
        }
        if (uri.indexOf("http") == 0) {
            var parser = document.createElement('a');
            parser.href = uri;
            return parser.hostname;
        }
    }
};

function setHash(newHash) {
    var sharpIdx = window.location.href.indexOf("#");
    if (sharpIdx === -1) {
        window.location.href = window.location.href + "#" + newHash;
    } else {
        window.location.href = window.location.href.substr(0, sharpIdx) + "#" + newHash;
    }
//    if (ga) {
//        ga('send', 'event', 'navigation', newHash);
//    }
}
if (!window.location.hash) {
    setHash("main");
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
    $("header").show();
    ga('send', 'pageview', "item/"+item);
    switchTo("item", function() {
        ii.item.load(item);
    });
}
function switchTo(id, callback){
    var container = $("#"+id+"-container");
    if (!container.length) {
        savePrevContent();
        container = $("<div id=\""+id+"-container\"></div>");
        container.hide();
        $("body").append(container);
        ensure({ html: id+".html", js: "js/"+id+".js", parent: id+"-container"}, function () {
            $("#content").append(container);
            container.show();
            if (callback) callback();
        });
    } else {
        if (!container.is(":visible")) {
            savePrevContent();
        }
        $("#content").append(container);
        container.show();
        if (callback) callback();
    }
}
function savePrevContent(){
    if (!$("#content").children().length) return;
    var prevContent = $("#content").children();
    $("body").append(prevContent);
    $("#content").empty();
    prevContent.hide();
    ii.prevScreen = prevContent.first().attr("id").replace("-container", "");
}

router.route("term::term", termRoute);
function termRoute(term) {
    $("header").show();
    term = term.replaceAll("+", " ");
    ga('send', 'pageview', "term/"+term);
    switchTo("term", function() {
        ii.term.load(term);
    });
}
router.route("search::query", searchRoute);
router.route("?:query", searchRoute);
function searchRoute(query) {
    $("header").show();
    query = query.replaceAll("+", " ");
    ga('send', 'pageview', "search/"+query);
    switchTo("search", function() {
        ii.search.load(query);
    });
}
router.route("a/:id", function(id) {
    $("header").show();
    ga('send', 'pageview', "article/"+id);
    switchTo("article", function() {
        ii.article.load(id);
    });
});
router.route("s/:id", function(id) {
    $("header").show();
    ga('send', 'pageview', "song/"+id);
    switchTo("song", function() {
        ii.song.load(id);
    });
});
router.route("about", function() {
    $("header").show();
    ga('send', 'pageview', "about");
    switchTo("about");
});
router.route("main", function() {
    $("header").hide();
    ga('send', 'pageview', "main");
    switchTo("main", function(){
        $("#search-input").kendoAutoComplete({
            minLength: 2,
            dataSource: {
                serverFiltering: true,
                serverPaging: true,
                pageSize: 20,
                transport: {
                    read: "/api/term/autocomplete"
                }
            },
            select: function(e) {
                var dataItem = this.dataItem(e.item.index());
                setHash(dataItem);
            }
        });
    })
});
router.route(":hash", function(hash) {
    if (isItemNumber(hash)) {
        itemRoute(hash)
    } else {
        termRoute(hash)
    }
});

$(document).ready(function() {
	function showHide(element_id) {
		if (document.getElementById(element_id)) { 
			var obj = document.getElementById(element_id); 
			if (obj.style.display != "block") { 
				obj.style.display = "block"; 
			}
			else obj.style.display = "none";
		}
	}

    var header = $("header");



    var box = header.find(".search-box");
    box.kendoAutoComplete({
        minLength: 3,
        dataSource: {
            serverFiltering: true,
            serverPaging: true,
            pageSize: 20,
            transport: {
                read: "/api/term/autocomplete"
            }
        },
        select: function(e) {
            var dataItem = this.dataItem(e.item.index());
            setHash(dataItem);
        }
    });
    box.keypress(function (e) {
        if (e.which == 13) {
            setHash("?"+box.val());
        }
    });

    router.start();
});