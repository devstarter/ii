//var originEncodeURIComponent = window.encodeURIComponent;
var app = angular.module('app', ['ui.router', 'ngSanitize', 'ui.bootstrap'])
    .config(function($locationProvider, $urlRouterProvider, $stateProvider, config) {
        if (config.useHtml5Mode) $locationProvider.html5Mode(true).hashPrefix('!');
        $urlRouterProvider.otherwise("home");
        //
//        // Now set up the states
        $stateProvider
            .state('home', {
                url: "/home",
                templateUrl: "partials/home.html",
                controller: HomeController,
                onEnter: function($rootScope){
                    $rootScope.$broadcast('home-state-entered');
                }
            })
            .state('article', {
                url: "/a/:id",
                templateUrl: "partials/article.html"
            })
            .state('item', {
                url: "/i/:number",
                templateUrl: "partials/item.html",
                controller: ItemController
            })
            .state('term', {
                url: "/t/:name",
                templateUrl: "partials/term.html",
                controller: TermController
            })
            .state('category', {
                url: "/c/:name",
                templateUrl: "partials/category.html",
                controller: CategoryController
            });

//        window.encodeURIComponent = function(arg) {
//            return originEncodeURIComponent(arg);
//        }
    })
    .factory("analytics", function(){
        return {
            registerEmptyTerm: function(termName) {
                if (typeof ga !== 'undefined') {
                    ga('send', 'event', 'no-data', termName);
                }
            }
        }
    })
    .factory("$api", function($rootScope, $state, $http, errorService, $q, config){
        var apiUrl = config.apiUrl;
//        var apiUrl = "https://ii.ayfaar.org/api/";
        var api = {
            post: function(url, data) {
                var deferred = $q.defer();
                var cache = data && typeof data._cache !== 'undefined' ? data._cache : false;
                if (data) delete data._cache;
                $http({
                    url: apiUrl+url,
                    data: data,
                    cache: cache,
                    method: "POST",
                    headers: { 'Content-Type': undefined },
                    transformRequest: function(data, getHeaders) {
                        var headers = getHeaders();
                        headers[ "Content-type" ] = "application/x-www-form-urlencoded; charset=utf-8";
                        return( serializePost( data ) );
                    }
                }).then(function(response){
                        deferred.resolve(response.data)
                    },function(response){
                        errorService.resolve(response.error);
                        deferred.reject(response);
                    });
                return deferred.promise;
            },
            get: function(url, data, skipError) {
                var deferred = $q.defer();
                var cache = data && typeof data._cache !== 'undefined' ? data._cache : false;
                if (data) delete data._cache;
                $http({
                    url: apiUrl+url+serializeGet(data),
                    method: "GET",
                    cache: cache
                }).then(function(response){
                        deferred.resolve(response.data)
                    },function(response){
                        if (!skipError) errorService.resolve(response.error);
                        deferred.reject(response);
                    });
                return deferred.promise;
            },
            item: {
                get: function(number) {
                    return api.get("v2/item", {number: number, _cache: true})
                },
                getContent: function(numberOrUri) {
                    numberOrUri = numberOrUri.replace("ии:пункт:", "");
                    return api.get("v2/item/"+numberOrUri+"/content")
                }
            },
            term: {
                get: function(name) {
                    return api.get('term/', {name: name, mark: true, _cache: true}, true);
                },
                getShortDescription: function(termName) {
                    return api.get("term/get-short-description", {name: termName, _cache: true})
                }
            },
            category: {
                get: function(name) {
                    return api.get('category', {name: name});
                }
            },
            search: {
                term: function(query) {
                    return api.get("search/term", {query: query})
                },
                content: function(query, page) {
                    return api.get("v2/search", {
                        query: query,
                        pageNumber: page
                    })
                },
                suggestions: function(query) {
                    return api.get("suggestions/"+query);
                }
            }
        };
        function serializeGet(obj) {
            var str = [];
            for(var p in obj) {
                if (obj.hasOwnProperty(p)) {
                    str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                }
            }
            return str.length ? "?"+str.join("&") : "";
        }
        function serializePost( data ) {
            // If this is not an object, defer to native stringification.
            if ( ! angular.isObject( data ) ) {
                return( ( data == null ) ? "" : data.toString() );
            }

            var buffer = [];

            // Serialize each key in the object.
            for ( var name in data ) {
                if ( ! data.hasOwnProperty( name ) ) {
                    continue;
                }
                var value = data[ name ];

                buffer.push(
                        encodeURIComponent( name ) +
                        "=" +
                        encodeURIComponent( ( value == null ) ? "" : value )
                );
            }

            // Serialize the buffer and clean it up for transportation.
            var source = buffer
                    .join( "&" )
                    .replace( /%20/g, "+" )
                ;
            return( source );
        }

        return api;
    })
    .factory("errorService", function($rootScope, $state){
        return {
            validationError: function(message) {
                return $ionicPopup.alert({
                    title: 'Ошибка',
                    template: message
                });
            },
            resolve: function(error) {
                var message = "Неизвесная ошибка";
                if (error) {
                    message = error.message;
                    switch (error.code) {
                        case "USER_NOT_FOUND":
                            message = "Пользователь не найден";
                            break;
                        case "PASSWORD_NOT_VALID":
                            message = "Пароль не верный";
                            break;
                        case "BAD_CREDENTIALS":
                            message = "Не верные email и пароль";
                            break;
                        case "EMAIL_DUPLICATION":
                            message = "Такой email уже зарегистрированн в системе";
                            break;
                    }
                }
                alert(message);
                /*return $ionicPopup.alert({
                    title: 'Ошибка',
                    template: message
                });*/
            }
        };
    })
    .factory('entityService', function(){
        var service = {
            getName: function (uri) {
                uri = uri.hasOwnProperty("uri") ? uri.uri : uri;
                switch (service.getType(uri)) {
                    case 'term':
                        return uri.replace("ии:термин:", "");
                    case 'item':
                        return uri.replace("ии:пункт:", "");
                }
            },
            getType: function(uri) {
                if (uri.indexOf("ии:термин:") === 0) {
                    return 'term'
                }
                if (uri.indexOf("ии:пункт:") === 0) {
                    return 'item'
                }
            }
        };
        return service;
    })
    .directive('entity', function($state, entityService) {
        return {
            restrict: 'E',
            replace: true,
            transclude: true,
            template: '<a href></a>',
            compile : function(element, attr, linker) {
                return function ($scope, $element, $attr) {
                    var entity = $scope[$attr.ngModel];
//                    var uiSref = "term({name:'"+name+"'})";
//                    $attr.$set('uiSref', uiSref);
                    $element.append(entityService.getName(entity));
                    $element.bind('click', function() {
                        $state.go(entity)
                    })
                }
            }
             /*link: function(scope, element, attrs) {
                var entity = scope[attrs.ngModel];
                var name = entity.uri.replace("ии:термин:", "");
                var uiSref = "term({name:'"+name+"'})";
                attrs.$set('uiSref', uiSref);
                element.removeAttr('ng-transclude');
                element.append(name);
                $compile(element)(scope);
            }*/
        };
    })
    .directive('iiLookup', function($api, $state) {
        return {
            require:'ngModel',
            link: function (originalScope, element, attrs, modelCtrl) {
                originalScope.$getSuggestions = function(query) {
                    return $api.get("suggestions/"+query)
                };
                originalScope.$suggestionSelected = function(suggestion) {
                    $state.goToTerm(suggestion);
                };
            }
        };
    })
    .directive('term', function($api, $state, $compile) {
        return {
            restrict: 'E',
            compile : function(element, attr, linker) {
                return function ($scope, $element, $attr) {
                    var term = $attr.id;
                    var primeTerm = $attr.title;
                    var originalContent = $element.html();
                    var expanded;
                    var hasShortDescription = $attr.hasShortDescription;
//                    $attr.title= "eee";
                    $element.bind('click', function(e) {
                        var more = e.target.tagName == "A";
                        var moreAfterPrimeTerm = e.target.id == "+";

                        if (more && !moreAfterPrimeTerm) {
                            $state.goToTerm(term);
                            return;
                        }
                        if (expanded && !moreAfterPrimeTerm) {
                            $element.html(originalContent);
                            expanded = false;
                        } else if (primeTerm && !moreAfterPrimeTerm)  {
                            expanded = true;
                            $element.append("&nbsp;("+primeTerm+" <i><a id=\"+\">детальнее</a></i>)");
//                            $element.append("&nbsp;(<term id=\""+primeTerm+"\">"+primeTerm+"</term>)");
//                            $compile($element.contents())($scope);
                        } else {
                            expanded = true;
                            if (hasShortDescription) {
                                var loadingPlaceHolder = "&nbsp;(загрузка...)";
                                $element.append(loadingPlaceHolder);
                                $api.term.getShortDescription(moreAfterPrimeTerm ? primeTerm : term).then(function (shortDescription) {
                                    $element.html(originalContent + " (" + shortDescription +
                                        " <i><a title=\"Перейти на детальное описание термина\">детальнее</a></i>)");
                                });
                            } else {
                                $element.html(originalContent + " (нет короткого пояснения, <i><a>детально</a></i>)");
                            }
                        }
                    })
                }
            }
        };
    })
    .directive("iiBind", function($compile) {
        // inspired by http://stackoverflow.com/a/25516311/975169
        return {
            link: function(scope, element, attrs) {
                scope.$watch(attrs.iiBind, function(newval) {
                    if (!newval) {
                        element.html(newval);
                        return;
                    }
                    newval = newval.replace(new RegExp("\\(([^\\)]+)\\)","gm"), "<bracket>$1</bracket>");
                    element.html(newval);
                    $compile(element.contents())(scope);
                });
            }
        };
    })
    .directive("bracket", function($compile) {
        return {
            restrict: 'E',
            scope: {},
            compile: function (element, attr, linker) {
                return function ($scope, $element, $attr) {
                    var bracketSpan = "<span ng-click='collapse = !collapse' ng-class='{highlite: h}' ng-mouseover='h = true' ng-mouseleave='h = false'>";
                    // предотвращение глюка вставки HTML
                    //var html = $element.html().replace("<strong>", "").replace("</strong>", "");
                    var html = "<span class='bracket'>"
                        +bracketSpan+"(</span>"
                        +"<span ng-click='collapse = !collapse' ng-show='collapse'>...</span>"
                        +"<span ng-hide='collapse'>"+$element.html()+"</span>"
                        +bracketSpan+")</span>"
                    +"</span>";
                    $element.html("");
                    $element.append($compile(html)($scope));
                }
            }
        }
    })
    .factory('focus', function($timeout) {
        return function(id) {
            // timeout makes sure that is invoked after any other event has been triggered.
            // e.g. click events that need to run before the focus or
            // inputs elements that are in a disabled state but are enabled when those events
            // are triggered.
            $timeout(function() {
                var element = document.getElementById(id);
                if(element)
                    element.focus();
            });
        };
    })
    .directive("iiHeader", function($rootScope, focus) {
        return {
            scope: {},
            templateUrl: "partials/header.html",
            link: function(scope, element, attrs) {
                scope.visible = true;
                scope.focus = focus;
                $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams){
                    if (toState.name != "home") {
                        scope.visible = true;
                    }
                });
                $rootScope.$on('home-state-entered', function() {
                    scope.visible = false;
                })
            }
        };
    })
    .controller("HeaderController", function() {

        /*var body = angular.element(document);//.find('body');

        $scope.alert = function(e) {
            alert(e);
        };
        body.bind('mouseup', function(e) {
            var text = getSelectedText();
            if (text) {
                $scope.state = $state.current.name+$stateParams.number;
                $scope.text = text;
                $scope.$apply();
            }
        });

        function getSelectedText() {
            if (window.getSelection) {
                return window.getSelection().toString();
            } else if (document.selection) {
                return document.selection.createRange().text;
            }
            return null;
        }*/
    })
    .run(function($state, entityService){
        var originStateGo = $state.go;
        $state.go = function(to, params, options) {
            if (to.hasOwnProperty('uri') || angular.isString(to)) {
                var uri = angular.isString(to) ? to : to.uri;
                switch (entityService.getType(uri)) {
                    case "term":
                        originStateGo.bind($state)("term", {name: entityService.getName(uri)});
                        return;
                    case "item":
                        originStateGo.bind($state)("item", {number: entityService.getName(uri)});
                        return;
                }
            } else {
                originStateGo.bind($state)(to, params, options)
            }
        };
        $state.goToItem = function(number) {
            originStateGo.bind($state)("item", {number: number})
        };
        $state.goToTerm = function(name) {
            originStateGo.bind($state)("term", {name: name})
        };
    })
    .filter('cut', function () {
        return function (value, wordwise, max, tail) {
            if (!value) return '';

            max = parseInt(max, 10);
            if (!max) return value;
            if (value.length <= max) return value;

            value = value.substr(0, max);
            if (wordwise) {
                var lastspace = value.lastIndexOf(' ');
                if (lastspace != -1) {
                    value = value.substr(0, lastspace);
                }
            }

            return value + (tail || ' …');
        };
    });

Array.prototype.append = function(array){
    this.push.apply(this, array)
};
function copyObjectTo(from, to) {
    for (var p in from) {
        if (from.hasOwnProperty(p)) {
            to[p] = from[p];
        }
    }
}

function isItemNumber(s) {
    return s.match("\\d+\\.\\d+");
}
