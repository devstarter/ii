angular.module('app', ['ui.router', 'ngSanitize', 'ui.bootstrap'])
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

        api.item = {
            get: function(number) {
                var deferred = $q.defer();
                // заглушка для тестирования
                deferred.resolve("Причём <term id=\"ра\" title=\"резонационная Активность\">РА</term> это «<term id=\"резонационная Активность\">квантовое схлопывание</term>» (резонационное «совмещение» лийллусцивных участков сллоогрентной ф-Конфигурации) может происходить на любом из Уровней проявления «личностного» Самосознания, начиная с самых низших, заканчивая самыми качественными (для данной НУУ-ВВУ-Конфигурации). При высокой коварллертности СФУУРММ-Форм, структурирующих какое-то из Направлений Фокусных Динамик «текущей» и «следующей» «личностной» Интерпретации, они синтезируются до общего для них состояния лийллусцивности и благодаря этому у Формо-Творцов какого-то из других участков «новой» НУУ-ВВУ-Конфигурации (не обязательно дувуйллерртных с данным резопазоном), за счёт только что добавленного фрагмента Информации, в данном режиме проявления образуются реальные возможности для формирования наиболее высокой коварллертности (почти лийллусцивности), что предопределяет следующую мгновенную активность Формо-Творцов Фокусной Динамики (то есть очередной акт «квантового смещения») именно в данном конкретном резопазоне. Этот помгновенный процесс «очаговых» (в случае активизации множества дувуйллерртных участков) или «точечных» (при резонации недувуйллерртных резопазонов) трансмутаций осуществляется в нашей Фокусной Динамике непрерывно и бесконечно.");
                return deferred.promise;
            }
        };
        api.term = {
            getShortDescription: function(termName) {
                return api.get("term/get-short-description", {name: termName})
            }
        };

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
                    return $api.get("v2/suggestions/"+query)
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
    .directive("unsecureBind", function($compile) {
        // inspired by http://stackoverflow.com/a/25516311/975169
        return {
            link: function(scope, element, attrs) {
                scope.$watch(attrs.unsecureBind, function(newval) {
                    element.html(newval);
                    $compile(element.contents())(scope);
                });
            }
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
        };
        $state.goToTerm = function(name) {
            defStateGo.bind($state)("term", {name: name})
        }
    });

Array.prototype.append = function(array){
    this.push.apply(this, array)
};

function isItemNumber(s) {
    return s.match("\\d+\\.\\d+");
}
