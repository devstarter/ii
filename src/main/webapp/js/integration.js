var init, maybeLoadJq, url = "http://ii.ayfaar.org/";

function jqueryLoaded() {
    var termsContainer = $(".ii-terms-container").first();
    $.post(url+"api/integration", termsContainer.text(), function(response){
        var terms = response.entryList;
        var html = termsContainer.html();
        for(var i in terms) {
            var key = terms[i].key;
            var term = terms[i].value;
            var re = new RegExp('(<a[^<]+)?'+key, 'gi');
            html = html.replace(re, function($0,$1){ return $1 ? $0 : '<a href="'+url+term+'" target="_blank" class="ii-term">'+RegExp.lastMatch+'</a>';});
        }
        termsContainer.html(html);
    });
}


init = function() {
    jQuery(document).ready(function() {
        jqueryLoaded();
    });
};

maybeLoadJq = function() {
    var jQ;
    if (!(typeof $ !== "undefined" && $ !== null)) {
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
