//var originEncodeURIComponent = window.encodeURIComponent;
angular.module('app', ['ui.router', 'ngSanitize', 'ui.bootstrap'])
    .config(function($locationProvider, $urlRouterProvider, $stateProvider) {
        $locationProvider.html5Mode(true).hashPrefix('!');
        $urlRouterProvider.otherwise("home");
        //
//        // Now set up the states
        $stateProvider
            .state('home', {
                url: "/home",
                templateUrl: "partials/home.html",
                controller: HomeController
            })
            .state('article', {
                url: "/a/:id",
                templateUrl: "partials/article.html"
            })
            .state('item', {
                url: "/item/:number",
                templateUrl: "partials/item.html",
                controller: ItemController
            })
            .state('term', {
                url: "/term/:name",
                templateUrl: "partials/term.html",
                controller: TermController
            })
            .state('cat', {
                url: "/cat/:name",
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
    .factory("$api", function($rootScope, $state, $http, errorService, $q){
        var apiUrl = "/api/";
//        var apiUrl = "http://localhost:8081/api/";
        var api = {
            post: function(url, data) {
                var deferred = $q.defer();
                $http.post(apiUrl+url, data, {
                    headers: { 'Content-Type': undefined },
                    transformRequest: function(data, getHeaders) {
                        var headers = getHeaders();
                        headers[ "Content-type" ] = "application/x-www-form-urlencoded; charset=utf-8";
                        return( serializePost( data ) );
                    }
                })
                    .then(function(response){
                        deferred.resolve(response.data)
                    },function(response){
                        errorService.resolve(response.error);
                        deferred.reject(response);
                    });
                return deferred.promise;
            },
            get: function(url, data, skipError) {
                var deferred = $q.defer();
                $http.get(apiUrl+url+serializeGet(data))
                    .then(function(response){
                        deferred.resolve(response.data)
                    },function(response){
                        if (!skipError) errorService.resolve(response.error);
                        deferred.reject(response);
                    });
                return deferred.promise;
            },
            item: {
                get: function(number) {
                    return api.get("item/"+number+"/")
                }
            },
            term: {
                get: function(name) {
                    return api.get('term/', {name: name}, true);
                },
                getShortDescription: function(termName) {
                    return api.get("term/get-short-description", {name: termName})
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
            getName: function (entity) {
                switch (service.getType(entity)) {
                    case 'term':
                        return entity.uri.replace("ии:термин:", "");
                    case 'item':
                        return entity.uri.replace("ии:пункт:", "");
                }
            },
            getType: function(entity) {
                if (entity.uri.indexOf("ии:термин:") === 0) {
                    return 'term'
                }
                if (entity.uri.indexOf("ии:пункт:") === 0) {
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
                    $state.go("term", {name: suggestion});
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
                    $attr.title= "eee";
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
                            $element.append("&nbsp;("+primeTerm+"<a id=\"+\">...</a>)");
//                            $element.append("&nbsp;(<term id=\""+primeTerm+"\">"+primeTerm+"</term>)");
//                            $compile($element.contents())($scope);
                        } else {
                            expanded = true;
                            var loadingPlaceHolder = "&nbsp;(загрузка...)";
                            $element.append(loadingPlaceHolder);
                            $api.term.getShortDescription(moreAfterPrimeTerm ? primeTerm : term).then(function (shortDescription) {
                                $element.html(originalContent + " (" + shortDescription +
                                    "<a title=\"Перейти на детальное описание термина\">...</a>)");
                            });
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
                    var span = "<span class='bracket' ng-click='collapse = !collapse'><span ng-click='collapse = !collapse' ng-class='{highlite: h}' ng-mouseover='h = true' ng-mouseleave='h = false'>";
                    var html = span+"(</span>{{collapse ? '...' : '"+$element.html()+"'}}"+span+")</span></span>";
                    $element.html("");
                    $element.append($compile(html)($scope));
                }
            }
        }
    })
    .run(function($state, entityService){
        var originStateGo = $state.go;
        $state.go = function(to, params, options) {
            if (to.hasOwnProperty('uri')) {
                var uri = to.uri;
                switch (entityService.getType(to)) {
                    case "term":
                        originStateGo.bind($state)("term", {name: entityService.getName(to)});
                        return;
                    case "item":
                        originStateGo.bind($state)("item", {number: entityService.getName(to)});
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
        }
    });

Array.prototype.append = function(array){
    this.push.apply(this, array)
};
function copyObjectTo(from, to) {
    for (var p in from) {
        to[p] = from[p];
    }
};

function isItemNumber(s) {
    return s.match("\\d+\\.\\d+");
}
