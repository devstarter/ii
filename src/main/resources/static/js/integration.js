var init, maybeLoadJq, url = "http://ii.ayfaar.org/";

function jqueryLoaded() {
    jQuery.fn.extend({
        getPath: function() {
            var pathes = [];

            this.each(function(index, element) {
                var path, $node = jQuery(element);

                while ($node.length) {
                    var realNode = $node.get(0), name = realNode.localName;
                    if (!name) { break; }

                    name = name.toLowerCase();
                    var parent = $node.parent();
                    var sameTagSiblings = parent.children(name);

                    if (sameTagSiblings.length > 1)
                    {
                        allSiblings = parent.children();
                        var index = allSiblings.index(realNode) +1;
                        if (index > 0) {
                            name += ':nth-child(' + index + ')';
                        }
                    }

                    path = name + (path ? '>' + path : '');
                    $node = parent;
                }

                pathes.push(path);
            });

            return pathes.join(',');
        }
    });
    jQuery(".itemFullText, .ii-terms-container").each(function() {
        var container = jQuery(this);
        jQuery.post(url + "api/integration?id=" + container.getPath(), container.text(), function (response) {
            var terms = response.entryList;
            var html = container.html();
            html = html.replace(/<img[^>]*>/g,'');
            for (var i in terms) {
                var key = terms[i].key;
                var term = terms[i].value;
                var re = new RegExp('(<a[^<]+)?(([^A-Za-zА-Яа-я0-9Ёё])|^|\\[|\\|)(' + key + ')(([^A-Za-zА-Яа-я0-9Ёё])|$|\\]|\\|)', 'gi');
                html = html.replace(re, function ($0, $1, $2, $3, $4, $5) {
                    return $1 ? $0 : $2 + '<a href="' + url + term + '" target="_blank" class="ii-term">' + $4 + '</a>' + $5;
                });
            }
            container.html(html);
        })
    })
}


init = function() {
    jQuery(document).ready(function() {
        jqueryLoaded();
    });
};

maybeLoadJq = function() {
    var jQ;
    if (!(typeof jQuery !== "undefined" && jQuery !== null)) {
        jQ = document.createElement('script');
        jQ.type = 'text/javascript';
        jQ.onload = jQ.onreadystatechange = init;
        jQ.src = '//ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js';
        return document.body.appendChild(jQ);
    } else {
        return init();
    }
};

if (window.addEventListener) {
    window.addEventListener('load', maybeLoadJq, false);
} else if (window.attachEvent) {
    window.attachEvent('onload', maybeLoadJq);
}
