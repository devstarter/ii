/*
Script: Ensure.js

Ensure library
	A tiny javascript library that provides a handy function "ensure" which allows you to load 
	Javascript, HTML, CSS on-demand and then execute your code. Ensure ensures that relevent 
	Javascript and HTML snippets are already in the browser DOM before executing your code 
	that uses them.
	
	To download last version of this script use this link: <http://www.codeplex.com/ensure>

Version:
	1.0 - Initial release

Compatibility:
	FireFox - Version 2 and 3
	Internet Explorer - Version 6 and 7
	Opera - 9 (probably 8 too)
	Safari - Version 2 and 3 
	Konqueror - Version 3 or greater

Dependencies:
	<jQuery.js> 
	<MicrosoftAJAX.js>
	<Prototype-1.6.0.js>

Credits:
	- Global Javascript execution - <http://webreflection.blogspot.com/2007/08/global-scope-evaluation-and-dom.html>
	
Author:
	Omar AL Zabir - http://msmvps.com/blogs/omar

License:
	>Copyright (C) 2008 Omar AL Zabir - http://msmvps.com/blogs/omar
	>	
	>Permission is hereby granted, free of charge,
	>to any person obtaining a copy of this software and associated
	>documentation files (the "Software"),
	>to deal in the Software without restriction,
	>including without limitation the rights to use, copy, modify, merge,
	>publish, distribute, sublicense, and/or sell copies of the Software,
	>and to permit persons to whom the Software is furnished to do so,
	>subject to the following conditions:
	>
	>The above copyright notice and this permission notice shall be included
	>in all copies or substantial portions of the Software.
	>
	>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
	>INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	>FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
	>IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
	>DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
	>ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
	>OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

(function(){

window.ensure = function( data, callback, scope )
{    
    if( typeof jQuery == "undefined" && typeof Sys == "undefined" && typeof Prototype == "undefined" )
        return alert("jQuery, Microsoft ASP.NET AJAX or Prototype library not found. One must be present for ensure to work");
        
    // There's a test criteria which when false, the associated components must be loaded. But if true, 
    // no need to load the components
    if( typeof data.test != "undefined" )
    {
        var test = function() { return data.test };
        
        if( typeof data.test == "string" )
        {
            test = function() 
            { 
                // If there's no such Javascript variable and there's no such DOM element with ID then
                // the test fails. If any exists, then test succeeds
                return !(eval( "typeof " + data.test ) == "undefined" 
                    && document.getElementById(data.test) == null); 
            }
        }    
        else if( typeof data.test == "function" )      
        {
            test = data.test;
        }
        
        // Now we have test prepared, time to execute the test and see if it returns null, undefined or false in any 
        // scenario. If it does, then load the specified javascript/html/css    
        if( test() === false || typeof test() == "undefined" || test() == null ) 
            new ensureExecutor(data, callback, scope);
        // Test succeeded! Just fire the callback
        else
            callback();
    }
    else
    {
        // No test specified. So, load necessary javascript/html/css and execute the callback
        new ensureExecutor(data, callback, scope);
    }
}

// ensureExecutor is the main class that does the job of ensure.
window.ensureExecutor = function(data, callback, scope)
{
    this.data = this.clone(data);
    this.callback = (typeof scope == "undefined" || null == scope ? callback : this.delegate(callback, scope));
    this.loadStack = [];
    
    if( data.js && data.js.constructor != Array ) this.data.js = [data.js];
    if( data.html && data.html.constructor != Array ) this.data.html = [data.html];
    if( data.css && data.css.constructor != Array ) this.data.css = [data.css];
    
    if( typeof data.js == "undefined" ) this.data.js = [];
    if( typeof data.html == "undefined" ) this.data.html = [];
    if( typeof data.css == "undefined" ) this.data.css = [];
    
    this.init();
    this.load();
}

window.ensureExecutor.prototype = {
    init : function()
    {
        // Fetch Javascript using Framework specific library
        if( typeof jQuery != "undefined" )
        {
            this.getJS = HttpLibrary.loadJavascript_jQuery;
            this.httpGet = HttpLibrary.httpGet_jQuery;
        }
        else if( typeof Prototype != "undefined" )
        {   
            this.getJS = HttpLibrary.loadJavascript_Prototype;
            this.httpGet = HttpLibrary.httpGet_Prototype; 
        }
        else if( typeof Sys != "undefined" )
        {
            this.getJS = HttpLibrary.loadJavascript_MSAJAX;
            this.httpGet = HttpLibrary.httpGet_MSAJAX;
        }
        else
        {
            throw "jQuery, Prototype or MS AJAX framework not found";
        }        
    },
    getJS : function(data)
    {
        // abstract function to get Javascript and execute it
    },
    httpGet : function(url, callback)
    {
        // abstract function to make HTTP GET call
    },    
    load : function()
    {
        this.loadJavascripts( this.delegate( function() { 
            this.loadCSS( this.delegate( function() { 
                this.loadHtml( this.delegate( function() { 
                    this.callback() 
                } ) ) 
            } ) ) 
        } ) );        
    },
    loadJavascripts : function(complete)
    {
        var scriptsToLoad = this.data.js.length;
        if( 0 === scriptsToLoad ) return complete();
        
        this.forEach(this.data.js, function(href)
        {
            if( HttpLibrary.isUrlLoaded(href) || this.isTagLoaded('script', 'src', href) )
            {
                scriptsToLoad --;
            }
            else
            {
                this.getJS({
                    url:        href, 
                    success:    this.delegate(function(content)
                                {
                                    scriptsToLoad --; 
                                    HttpLibrary.registerUrl(href);
                                }), 
                    error:      this.delegate(function(msg)
                                {
                                    scriptsToLoad --; 
                                    if(typeof this.data.error == "function") this.data.error(href, msg);
                                })
                });
            }            
        });
        
        // wait until all the external scripts are downloaded
        this.until({ 
            test:       function() { return scriptsToLoad === 0; }, 
            delay:      50,
            callback:   this.delegate(function()
            {
                complete();
            })
        });
    },    
    loadCSS : function(complete)
    {
        if( 0 === this.data.css.length ) return complete();
        
        var head = HttpLibrary.getHead();
        this.forEach(this.data.css, function(href)
        {
            if( HttpLibrary.isUrlLoaded(href) || this.isTagLoaded('link', 'href', href) )
            {
                // Do nothing
            }
            else
            {            
                var self = this;
                try
                {   
                    (function(href, head)
                    {                             
                        var link = document.createElement('link');
                        link.setAttribute("href", href);
                        link.setAttribute("rel", "Stylesheet");
                        link.setAttribute("type", "text/css");
                        head.appendChild(link);
                    
                        HttpLibrary.registerUrl(href);
                    }).apply(window, [href, head]);
                }
                catch(e)
                {
                    if(typeof self.data.error == "function") self.data.error(href, e.message);
                }                
            }
        });
        
        complete();
    },
    loadHtml : function(complete)
    {
        var htmlToDownload = this.data.html.length;
        if( 0 === htmlToDownload ) return complete();
        
        this.forEach(this.data.html, function(href)
        {
            if( HttpLibrary.isUrlLoaded(href) )
            {
                htmlToDownload --;
            }
            else
            {
                this.httpGet({
                    url:        href, 
                    success:    this.delegate(function(content)
                                {
                                    htmlToDownload --; 
                                    HttpLibrary.registerUrl(href);
                                    
                                    var parent = (this.data.parent || document.body.appendChild(document.createElement("div")));
                                    if( typeof parent == "string" ) parent = document.getElementById(parent);
                                    parent.innerHTML = content;
                                }), 
                    error:      this.delegate(function(msg)
                                {
                                    htmlToDownload --; 
                                    if(typeof this.data.error == "function") this.data.error(href, msg);
                                })
                });
            }            
        });
        
        // wait until all the external scripts are downloaded
        this.until({ 
            test:       function() { return htmlToDownload === 0; }, 
            delay:      50,
            callback:   this.delegate(function()
            {                
                complete();
            })
        });
    },
    clone : function(obj)
    {
        var cloned = {};
        for( var p in obj )
        {
            var x = obj[p];
                
            if( typeof x == "object" )
            {
                if( x.constructor == Array )
                {
                    var a = [];
                    for( var i = 0; i < x.length; i ++ ) a.push(x[i]);
                    cloned[p] = a;
                }
                else
                {
                    cloned[p] = this.clone(x);
                }
            }
            else
                cloned[p] = x;
        }
        
        return cloned;
    },
    forEach : function(arr, callback)
    {
        var self = this;
        for( var i = 0; i < arr.length; i ++ )
            callback.apply(self, [arr[i]]);
    },
    delegate : function( func, obj )
    {
        var context = obj || this;
        return function() { func.apply(context, arguments); }
    },
    until : function(o /* o = { test: function(){...}, delay:100, callback: function(){...} } */)
    {
        if( o.test() === true ) o.callback();
        else window.setTimeout( this.delegate( function() { this.until(o); } ), o.delay || 50);
    },
    isTagLoaded : function(tagName, attName, value)
    {
        // Create a temporary tag to see what value browser eventually 
        // gives to the attribute after doing necessary encoding
        var tag = document.createElement(tagName);
        tag[attName] = value;
        var tagFound = false;
        var tags = document.getElementsByTagName(tagName);
        this.forEach(tags, function(t) 
        { 
            if( tag[attName] === t[attName] ) { tagFound = true; return false } 
        });
        return tagFound;
    }
}

var userAgent = navigator.userAgent.toLowerCase();

// HttpLibrary is a cross browser, cross framework library to perform common operations
// like HTTP GET, injecting script into DOM, keeping track of loaded url etc. It provides
// implementations for various frameworks including jQuery, MSAJAX or Prototype
var HttpLibrary = {
    browser : {
	    version: (userAgent.match( /.+(?:rv|it|ra|ie)[\/: ]([\d.]+)/ ) || [])[1],
	    safari: /webkit/.test( userAgent ),
	    opera: /opera/.test( userAgent ),
	    msie: /msie/.test( userAgent ) && !/opera/.test( userAgent ),
	    mozilla: /mozilla/.test( userAgent ) && !/(compatible|webkit)/.test( userAgent )
    },
    loadedUrls : {},
    
    isUrlLoaded : function(url)
    {
        return HttpLibrary.loadedUrls[url] === true;
    },
    unregisterUrl : function(url)
    {
        HttpLibrary.loadedUrls[url] = false;
    },
    registerUrl : function(url)
    {
        HttpLibrary.loadedUrls[url] = true;
    },
    
    createScriptTag : function(url, success, error)
    {
        var scriptTag = document.createElement("script");
        scriptTag.setAttribute("type", "text/javascript");
        scriptTag.setAttribute("src", url);
        scriptTag.onload = scriptTag.onreadystatechange = function()
        {
            if ( (!this.readyState || 
					this.readyState == "loaded" || this.readyState == "complete") ) {
				success();
			}
		};
        scriptTag.onerror = function()
        {
            error(data.url + " failed to load");
        };
	    var head = HttpLibrary.getHead();
        head.appendChild(scriptTag);
    },
    getHead : function()
    {
        return document.getElementsByTagName("head")[0] || document.documentElement
    },
    globalEval : function(data)
    {
        var script = document.createElement("script");
        script.type = "text/javascript";
		if ( HttpLibrary.browser.msie )
			script.text = data;
		else
			script.appendChild( document.createTextNode( data ) );

        var head = HttpLibrary.getHead();
		head.appendChild( script );
		//head.removeChild( script );
    },
    loadJavascript_jQuery : function(data)
    {
        if( HttpLibrary.browser.safari )
        {
           return jQuery.ajax({
			    type:       "GET",
			    url:        data.url,
			    data:       null,
			    success:    function(content)
			                {
			                    HttpLibrary.globalEval(content);
			                    data.success();
			                },
			    error:      function(xml, status, e) 
                            { 
                                if( xml && xml.responseText )
                                    data.error(xml.responseText);
                                else
                                    data.error(url +'\n' + e.message);
                            },
			    dataType: "html"
		    });
        }
        else
        {
            HttpLibrary.createScriptTag(data.url, data.success, data.error);
        }
    },    
    loadJavascript_MSAJAX : function(data)
    {
        if( HttpLibrary.browser.safari )
        {
            var params = 
            { 
                url: data.url, 
                success: function(content)
                {
                    HttpLibrary.globalEval(content);
                    data.success(content);
                },
                error : data.error 
            };
            HttpLibrary.httpGet_MSAJAX(params);
        }
        else
        {
            HttpLibrary.createScriptTag(data.url, data.success, data.error);
        }
    },
    loadJavascript_Prototype : function(data)
    {
        if( HttpLibrary.browser.safari )
        {
            var params = 
            { 
                url: data.url, 
                success: function(content)
                {
                    HttpLibrary.globalEval(content);
                    data.success(content);
                },
                error : data.error 
            };
            HttpLibrary.httpGet_Prototype(params);
        }
        else
        {
            HttpLibrary.createScriptTag(data.url, data.success, data.error);
        }        
    },
    httpGet_jQuery : function(data)
    {
        return jQuery.ajax({
			type:       "GET",
			url:        data.url,
			data:       null,
			success:    data.success,
			error:      function(xml, status, e) 
                        { 
                            if( xml && xml.responseText )
                                data.error(xml.responseText);
                            else
                                data.error("Error occured while loading: " + url +'\n' + e.message);
                        },
			dataType: data.type || "html"
		});
    },
    httpGet_MSAJAX : function(data)
    {
        var _wRequest =  new Sys.Net.WebRequest();
        _wRequest.set_url(data.url);
        _wRequest.set_httpVerb("GET");
        _wRequest.add_completed(function (result) 
        {
            var errorMsg = "Failed to load:" + data.url;
            if (result.get_timedOut()) {
                errorMsg = "Timed out";
            }
            if (result.get_aborted()) {
                errorMsg = "Aborted";
            }
            
            if (result.get_responseAvailable()) data.success( result.get_responseData() );
            else data.error( errorMsg );
        });

        var executor = new Sys.Net.XMLHttpExecutor();
        _wRequest.set_executor(executor); 
        executor.executeRequest();
    },
    httpGet_Prototype : function(data)
    {
        new Ajax.Request(data.url, {
            method:     'get',
            evalJS:     false,  // Make sure prototype does not automatically evan scripts
            onSuccess:  function(transport, json)
                        {
                            data.success(transport.responseText || "");              
                        },
            onFailure : data.error
        });
    }
};

})();