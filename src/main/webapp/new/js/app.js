angular.module('app', ['ui.router', 'live-search', 'ngSanitize'])
    .config(function($locationProvider, $urlRouterProvider, $stateProvider) {
//        $locationProvider.html5Mode(true).hashPrefix('!');

        $urlRouterProvider.otherwise("/home");
        //
//        // Now set up the states
        $stateProvider
            .state('home', {
                url: "/home",
                templateUrl: "partials/home.html",
                controller: HomeController
            })
            .state('search', {
                url: "/search/:query",
                templateUrl: "partials/search.html",
                controller: SearchController
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
//                onEnter: function($location, $stateParams, $log){
//                    $log.info($stateParams)
//                }
            });
    })
    .factory("analytics", function(){
        return {
            registerEmptyTerm: function(termName) {
                if (ga) {
                    ga('send', 'event', 'no-data', termName);
                }
            }
        }
    })
    .factory("$api", function($rootScope, $state, $http, errorService, $q){
        var apiUrl = "http://ii.ayfaar.org/api/";
//        var apiUrl = "http://localhost:8081/api/";
        return {
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
            get: function(url, data) {
                var deferred = $q.defer();
                $http.get(apiUrl+url+serializeGet(data))
                    .then(function(response){
                        deferred.resolve(response.data)
                    },function(response){
                        errorService.resolve(response.error);
                        deferred.reject(response);
                    });
                return deferred.promise;
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
    .run(function($state, entityService){
        var defStateGo = $state.go;
        $state.go = function(to, params, options) {
            if (to.hasOwnProperty('uri')) {
                var uri = to.uri;
                if (entityService.getType(to) == "term") {
                    defStateGo.bind($state)("term", {name: entityService.getName(to)})
                }
            } else {
                defStateGo.bind($state)(to, params, options)
            }
        };
        $state.goToItem = function(number) {
            defStateGo.bind($state)("item", {number: number})
        }
    });

Array.prototype.append = function(array){
    this.push.apply(this, array)
};

function isItemNumber(s) {
    return s.match("\\d+\\.\\d+");
}
