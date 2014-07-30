angular.module('app', ['ui.router', 'live-search'])
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
                templateUrl: "partials/search.html"
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
            })
    })
    .factory("$api", function($rootScope, $state, $http, errorService, $q){
        var apiUrl = "http://ii.ayfaar.org/api/v2/";
//        var apiUrl = "http://localhost:8081/";
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
    });

Array.prototype.append = function(array){
    this.push.apply(this, array)
};
