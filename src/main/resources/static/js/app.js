var hash = window.location.hash;
if (hash) {
    window.location.hash = '';
    window.location.pathname = window.location.pathname + hash.replace("#?", "").replace("#", "");
}
var app = angular.module('app', ['ui.router', 'ngResource', 'ngSanitize', 'ngCookies', 'ui.bootstrap', 'ngAudio'])
    .config(function($locationProvider, $urlRouterProvider, $stateProvider, config) {
        if (config.useHtml5Mode) $locationProvider.html5Mode(true).hashPrefix('!');
        $urlRouterProvider.otherwise("@");
        //
//        // Now set up the states
        $stateProvider
            .state('logout', {
                url: "/logout",
                controller: function($state, auth) {
                    auth.logout();
                    $state.goToHome()
                }
            })      
            .state('home', {
                url: "/{at: @?}",
                templateUrl: "static/partials/home.html",
                controller: HomeController,
                onEnter: function($rootScope){
                    $rootScope.$broadcast('home-state-entered');
                }
            })
            .state('knowledge-base', {
                url: "/kb",
                templateUrl: "static/partials/knowledge-base.html",
                controller: KnowledgeBaseController,
                onEnter: function($rootScope){
                    $rootScope.$broadcast('home-state-entered');
                }
            })
            .state('cabinet', {
                url: "/я",
                templateUrl: "static/partials/cabinet.html",
                controller: CabinetController
            })
            .state('document', {
                url: "/d/{id}",
                templateUrl: "static/partials/document.html",
                controller: DocumentController
            })
            .state('record', {
                url: "/r/{code}",
                templateUrl: "static/partials/record.html",
                controller: RecordController
            })
            .state('topic', {
                url: "/t/{name}",
                templateUrl: "static/partials/topic.html",
                controller: TopicController
            })
            .state('tagger', {
                url: "/tagger",
                templateUrl: "static/partials/tagger.html",
                controller: TaggerController
            })
            .state('resource-video', {
                url: "/resource/video/{id: \.*}",
                controller: function ($stateParams, $state) {
                    $state.goToVideo({id: $stateParams.id})
                }
            })
            .state('video', {
                url: "/v/{id: \.*}",
                templateUrl: "static/partials/resources.html",
                controller: ResourcesController
            })
            .state('article', {
                url: "/a/:id",
                templateUrl: "static/partials/article.html",
                controller: ArticleController
            })
            .state('item', {
                url: "/{number: \\s*\\d+\\.\\d+\\s*}",
                templateUrl: "static/partials/item.html",
                controller: ItemController
            })
            .state('item-range', {
                url: "/{from:\\d+\\.\\d+}{space1:\\s*}-{space2:\\s*}{to:\\d+\\.\\d+}",
                templateUrl: "static/partials/item-range.html",
                controller: ItemRangeController
            })
            .state('paragraph', {
                url: "/{number:\\d+\\.\\d+\\.\\d+\\.\\d+}",
                templateUrl: "static/partials/paragraph.html",
                controller: ParagraphController
            })
            .state('category', {
                url: "/c/*name",
                templateUrl: "static/partials/category.html",
                controller: CategoryController
            })
            .state('term', {
                url: "/:name",
                templateUrl: "static/partials/term.html",
                controller: TermController
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
            },
            pageview: function(url) {
                if (typeof ga !== 'undefined') {
                    ga('send', 'pageview', url);
                }
            }
        }
    })
    .factory("$api", function($rootScope, $state, $http, errorService, $q, config, $httpParamSerializer, $injector, modal, $cookies){
        var apiUrl = config.apiUrl;
//        var apiUrl = "https://ii.ayfaar.org/api/";
        function authenticate() {
            var auth = $injector.get("auth");
            if (auth.isAuthenticated()) {
                var d = $q.defer();
                d.resolve();
                return d.promise;
            } else {
                var wasAuthenticated = $cookies.getObject("auth_provider");
                return auth.authenticate(wasAuthenticated ? null : function () {
                    return modal.confirm("Действие нуждается в авторизации", "Представтесь пожалуйста системе для выполнения данного действия. Это займёт пару секунд.", "Представится")
                })
            }
        }
        function moderatedAction(response) {
            if (response.data.error.code == "CONFIRMATION_REQUIRED") {
                modal.message("Действие нуждается в подтверждении", "Данное действиет будет исполненно после подтверждения модератором системы");
                return true;
            }
        }
        var api = {
            authGet: function (url, data) {
                var deferred = $q.defer();
                authenticate().then(function () {
                    api.get(url, data).then(function (response) {
                        deferred.resolve(response)
                    }, function (error) {
                        deferred.reject(error)
                    })
                });
                return deferred.promise
            },
            authPost: function (url, data) {
                var deferred = $q.defer();
                authenticate().then(function () {
                    api.post(url, data).then(function (response) {
                        deferred.resolve(response)
                    }, function (error) {
                        deferred.reject(error)
                    })
                });
                return deferred.promise
            },
            post: function(url, data) {
                var deferred = $q.defer();
                var cache = data && typeof data._cache !== 'undefined' ? data._cache : false;
                if (data) delete data._cache;
                $http({
                    url: apiUrl+url,
                    data: data,
                    cache: cache,
                    method: "POST",
                    headers: { 'Content-Type': "application/x-www-form-urlencoded; charset=utf-8" },
                    transformRequest: $httpParamSerializer
                }).then(function(response){
                    deferred.resolve(response.data)
                },function(response){
                    if (!moderatedAction(response)) {
                        errorService.resolve(response.data.error);
                    }
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
                    cache: cache,
                    timeout: 300000
                }).then(function(response){
                    deferred.resolve(response.data)
                },function(response){
                    if (!moderatedAction(response) && !skipError) {
                        errorService.resolve(response.data.error);
                    }
                    deferred.reject(response);
                });
                return deferred.promise;
            },
            item: {
                get: function(number) {
                    return api.get("v2/item", {number: number, _cache: true})
                },
                getRange: function(from, to) {
                    return api.get("v2/item/range", {from: from, to: to, _cache: true})
                },
                getContent: function(numberOrUri) {
                    numberOrUri = numberOrUri.replace("ии:пункт:", "");
                    return api.get("v2/item/"+numberOrUri+"/content")
                }
            },
            term: {
                link: function(term1, term2, type) {
                    return api.authPost("link/addAlias", {term1: term1, term2: term2, type: type});
                },
                get: function(name) {
                    return api.get('term/', {name: name, mark: true, _cache: true}, true);
                },
                getShortDescription: function(termName) {
                    return api.get("term/get-short-description", {name: termName, _cache: true})
                },
                getTermsInText: function(text) {
                    return api.post("v2/term/get-terms-in-text", {text: text})
                }
            },
            category: {
                get: function(name) {
                    return api.get('category', {name: name, _cache: true});
                }
            },
            article: {
                get: function(id) {
                    return api.get('article/'+id, {_cache: true});
                }
            },
            search: {
                term: function(query) {
                    return api.get("search/term", {query: query})
                },
                content: function(query, startFrom, page) {
                    return api.get("v2/search", {
                        query: query,
                        startFrom: startFrom ? startFrom : "",
                        pageNumber: page
                    })
                },
                suggestions: function(query) {
                    return api.get("suggestions/"+query)
                },
                suggestionsAll: function (q) {
                    return api.get("suggestions/all", {q: q})
                },
                quote: function(uri, term, quote) {
                    return api.authPost("search/rate/+", {uri: uri, query: term, quote: quote})
                }
            },
            resource: {
                video: {
                    add: function (url) {
                        return api.authPost("resource/video", {url: url})
                    },
                    last: function (page, size) {
                        var params = {page: page};
                        if (size) params.size = size;
                        return api.get("resource/video/last-created", params)
                    }
                }
            },
            topic: {
                last: function (size) {
                    return api.get("topic/last", {size: size})
                },
                suggest: function (q) {
                    return api.get("topic/suggest", {q: q})
                },
                getFor: function (uri) {
                    return api.get("topic/for/"+uri)
                },
                merge: function (main, mergeInto) {
                    return api.authGet("topic/merge", {main: main, mergeInto: mergeInto})
                },
                updateComment: function (forUri, topicName, comment) {
                    return api.authPost("topic/update-comment", {forUri: forUri, name: topicName, comment: comment})
                },
                updateRate: function (forUri, topicName, rate) {
                    return api.authPost("topic/update-rate", {forUri: forUri, name: topicName, rate: rate})
                },
                addFor: function (objectUri, topicName, comment, rate) {
                    return api.authPost("topic/for", {name: topicName, uri: objectUri, comment: comment, rate: rate})
                },
                unlinkUri: function (objectUri, topicUri) {
                    return api.authPost("topic/unlink-uri", {topicUri: topicUri, uri: objectUri})
                },
                unlink: function (main, linked) {
                    return api.authPost("topic/unlink", {name: main, linked: linked})
                },
                suggest: function (q) {
                    return api.get("topic/suggest", {q: q})
                },
                /**
                 * @param name имя топика
                 * @param includeResources true|false
                 * @returns топик с родительскими и дочерними топиками, и ресурсами если требуется
                 */
                get: function (name, includeResources) {
                    return api.get("topic", {name: name, includeResources: includeResources ? "true" : "false"})
                },
                addChild: function (parent, child) {
                    return api.authGet("topic/add-child", {name: parent, child: child})
                }
            },
            document: {
                get: function (id) {
                    return api.get("document/"+id)
                },
                add: function (url) {
                    return api.authPost("document", {url: url})
                },
                last: function (page, size) {
                    var data = {page: page ? page : 0};
                    if (size) data.size = size;
                    return api.get("document/last", data)
                }
            },
            record: {
                last: function (page, size) {
                    var data = {page: page ? page : 0};
                    if (size) data.size = size;
                    return api.get("record", data)
                },
                get: function (page, nameOrCode, year, withUrl) {
                    var data = {page: page ? page : 0};
                    if (nameOrCode) data.nameOrCode = nameOrCode;
                    if (year) data.year = year;
                    if (withUrl) data.with_url = withUrl;
                    return api.get("record", data)
                }
            },
            auth: {
                registrate: function (user) {
                    return api.post("auth", user)
                }
            },
            user: {
                getCurrent: function () {
                    return api.get("user/current")
                },
                rename: function (newName) {
                    return api.authPost("user/current/rename", {name: newName})
                }
            },
            moderation: {
                pendingActions: function () {
                    return api.authGet("moderation/pending_actions")
                },
                confirm: function (id) {
                    return api.authGet("moderation/"+id+"/confirm")
                },
                cancel: function (id) {
                    return api.authPost("moderation/"+id+"/cancel")
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
        return api;
    })
    .factory('entityService', function(){
        var service = {
            getName: function (uriOrObject) {
                var uri, object;
                if (uriOrObject.hasOwnProperty("uri")) {
                    object = uriOrObject;
                    uri = object.uri;
                } else {
                    uri = uriOrObject;
                }
                switch (service.getType(uri)) {
                    case 'topic':
                        return uri.replace("тема:", "");
                    case 'term':
                        return uri.replace("ии:термин:", "");
                    case 'item':
                        return uri.replace("ии:пункт:", "");
                    case 'category':
                        return uri.replace("категория:", "");
                    case 'article':
                        return uri.replace("статья:", "");
                    case 'paragraph':
                        return uri.replace("ии:пункты:", "");
                    case 'record':
                        return uri.replace("запись:", "");
                    case 'document':
                        return uri.replace("документ:", "");
                    case 'video':
                        return object ? object.title : uri;
                }
            },
            getType: function(uri) {
                uri = uri.hasOwnProperty("uri") ? uri.uri : uri;
                if (uri.indexOf("тема:") === 0) {
                    return 'topic'
                }
                if (uri.indexOf("ии:термин:") === 0) {
                    return 'term'
                }
                if (uri.indexOf("ии:пункт:") === 0) {
                    return 'item'
                }
                if (uri.indexOf("категория:") === 0) {
                    return 'category'
                }
                if (uri.indexOf("статья:") === 0) {
                    return 'article'
                }
                if (uri.indexOf("видео:") === 0) {
                    return 'video'
                }
                if (uri.indexOf("документ:") === 0) {
                    return 'document'
                }
                if (uri.indexOf("ии:пункты:") === 0) {
                    return 'paragraph'
                }
                if (uri.indexOf("запись:") === 0) {
                    return 'record'
                }
            },
            getTypeLabel: function(uri) {
                switch (service.getType(uri)) {
                    case 'topic':
                        return "тема";
                    case 'term':
                        return "термин";
                    case 'item':
                        return "абзац";
                    case 'category':
                        return "оглавление";
                    case 'article':
                        return "статья";
                    case 'paragraph':
                        return "пераграф";
                    case 'video':
                        return "видео";
                    case 'document':
                        return "документ";
                    case 'record':
                        return "аудио";
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
                    $element.append(entity.hasOwnProperty("name") ? entity.name : entityService.getName(entity));
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
    .service('messager', function($rootScope) {
        $rootScope.alerts = [];
        $rootScope.closeAlert = function(index) {
            $rootScope.alerts.splice(index, 1);
        };
        return {
            ok: function (msg) {
                $rootScope.alerts.push({msg: msg, type: 'success'});
            },
            error: function (msg) {
                $rootScope.alerts.push({msg: msg, type: 'danger'});
            }
        }
    })
    .service('modal', function($modal) {
        return {
            confirm: function (title, text, action) {
                return $modal.open({
                    templateUrl: 'static/partials/modal-confirm.html',
                    controller: function ($scope, $modalInstance) {
                        $scope.title = title;
                        $scope.text = text;
                        $scope.action = action;
                        $scope.act = function() {
                            $modalInstance.close();
                        };
                        $scope.cancel = function() {
                            $modalInstance.dismiss('cancel');
                        };
                    }
                }).result;
            },
            prompt: function (title, text, action) {
                return $modal.open({
                    templateUrl: 'modal-prompt.html',
                    controller: function ($scope, $modalInstance) {
                        $scope.title = title;
                        $scope.text = text;
                        $scope.action = action;
                        $scope.act = function() {
                            $modalInstance.close($scope.text);
                        };
                        $scope.cancel = function() {
                            $modalInstance.dismiss('cancel');
                        };
                    }
                }).result;
            },
            message: function (title, text) {
                return $modal.open({
                    templateUrl: 'modal-message.html',
                    controller: function ($scope, $modalInstance) {
                        $scope.title = title;
                        $scope.text = text;
                        $scope.ok = function() {
                            $modalInstance.close();
                        };
                    }
                }).result;
            }
        }
    })
    .service('auth', function($modal, $api, $rootScope, $q, modal, $cookies) {
        return {
            logout: function () {
                delete $rootScope.user
            },
            isAuthenticated: function () {
                return typeof $rootScope.user !== 'undefined' ? $rootScope.user : false;
            },
            authenticate: function (runBeforeOpenAuthWindow) {
                var deferred = $q.defer();
                if ($rootScope.user)
                    deferred.resolve($rootScope.user);
                else
                    $api.user.getCurrent().then(function (user) {
                        if (user) {
                            $rootScope.user = user;
                            deferred.resolve(user);
                        }
                        else if (runBeforeOpenAuthWindow) {
                            runBeforeOpenAuthWindow().then(loadHelloAndOpenModal);
                        }
                        else {
                            loadHelloAndOpenModal();
                        }
                    });

                function loadHelloAndOpenModal() {
                    if (typeof hello === 'undefined') {
                        requirejs(["static/lib/hello/hello.min.js"], function (hello) {
                            hello.init({
                                facebook: "917074828411840",
                                vk: "5371182",
                                google: ""
                            }, {redirect_uri: "/static/lib/hello/redirect.html"});

                            openAuthModal()
                        });
                    } else {
                        openAuthModal()
                    }
                }
                function helloAuthenticate(provider) {
                    var deferred = $q.defer();
                    hello(provider).login({force: false, scope: 'email'}).then(function (auth) {
                        hello(provider).api('me').then(function (user) {
                            user = angular.merge({
                                access_token: auth.authResponse.access_token,
                                auth_provider: auth.network
                            }, user);
                            deferred.resolve(user);
                        });
                    });
                    return deferred.promise;
                }

                function openAuthModal() {
                    var provider = $cookies.getObject("auth_provider");
                    if (provider) {
                        helloAuthenticate(provider).then(function (user) {
                            $api.auth.registrate(user).then(function (user) {
                                $rootScope.user = user;
                                deferred.resolve(user);
                            });
                        });
                        return
                    }

                    $modal.open({
                        templateUrl: 'modal-auth.html',
                        controller: function ($scope, $modalInstance) {
                            $scope.authenticate = function(provider) {
                                helloAuthenticate(provider).then(function (user) {
                                    $scope.user = user;
                                    // $scope.$apply();
                                    $cookies.putObject("auth_provider", provider);
                                });
                            };
                            $scope.registrate = function () {
                                $api.auth.registrate($scope.user).then(function () {
                                    $rootScope.user = $scope.user;
                                    $modalInstance.close($scope.user);
                                    deferred.resolve($scope.user);
                                });
                            };
                            $scope.cancel = function () {
                                $modalInstance.dismiss('cancel');
                                deferred.reject();
                            };
                        }
                    })
                }
                return deferred.promise;
            }
        }
    })
    .service("errorService", function(messager){
        return {
            resolve: function(error) {
                var message = "Неизвесная ошибка";
                if (error) {
                    message = error.message;
                    switch (error.code) {
                        case "ACCESS_DENIED":
                            message = "Представтесь пожалуйста";
                            break;
                        case "USER_NOT_FOUND":
                            message = "Пользователь не найден";
                            break;
                        case "TOPIC_NOT_FOUND":
                            message = "Тема не найдена";
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
                messager.error(message);
            }
        };
    })
    .service('$topicPrompt', function($api, $modal, $topicSelector) {
        return {
            prompt: function () {
                return $modal.open({
                    templateUrl: 'static/partials/topic-prompt.html',
                    controller: function ($scope, $modalInstance) {
                        $scope.suggestTopics = function (q) {
                            return $api.topic.suggest(q);
                        };
                        $scope.select = function() {
                            $modalInstance.close($scope.topic);
                        };
                        $scope.cancel = function() {
                            $modalInstance.dismiss('cancel');
                        };
                        $scope.openSelector = function () {
                            $topicSelector.select().then(function (topicName) {
                                $modalInstance.close(topicName);
                            });
                        };
                    }
                }).result;
            }
        }
    })
    .service('$topicSelector', function($api, $modal) {
        return {
            select: function () {
                return $modal.open({
                    templateUrl: 'static/partials/topic-selector.html',
                    controller: function ($scope, $modalInstance) {
                        $scope.history = [];
                        $scope.select = function () {
                            $modalInstance.close($scope.name);
                        };
                        $scope.cancel = function () {
                            $modalInstance.dismiss('cancel');
                        };
                        $scope.load = function (topicName, dontSaveHistory) {
                            if ($scope.name && !dontSaveHistory) $scope.history.push($scope.name);
                            $scope.children = [];
                            $api.topic.get(topicName).then(function (topic) {
                                copyObjectTo(topic, $scope);
                            });
                        };
                        $scope.back = function () {
                            if ($scope.history.length)
                                $scope.load($scope.history.pop(), true);
                        };
                        $scope.load("Методика МИЦИАР");
                    }
                }).result
            }
        }
    })
    .directive('iiLookup', function($api, $state, $parse) {
        return {
            require:'ngModel',
            link: function (originalScope, element, attrs, modelCtrl) {
                originalScope.$getSuggestions = function(query) {
                    return $api.get("suggestions/"+query)
                };
                originalScope.$suggestionSelected = function(suggestion) {
                    $state.goToTerm(suggestion ? suggestion : originalScope.query);
                };
                var onEnter = $parse(attrs.onEnter);
                element.bind('keyup', function(event) {
                    if (event.keyCode == 13) {// enter
                        // originalScope.$suggestionSelected(event.target.value);
                        if (onEnter) onEnter(originalScope);
                    }
                })
            }
        };
    })
    .directive('iiRef', function($state, entityService, $parse) {
        return {
            link: function (scope, element, attrs, modelCtrl) {
                var getter = $parse(attrs.iiRef);
                var obj = getter(scope);//[attrs.iiRef];
                if (!obj) return;
                element.attr('href', getUrl(obj));
                var label = obj.hasOwnProperty("name") ? obj.name : entityService.getName(obj);
                obj._label = entityService.getType(obj) == 'paragraph' ? '§' + label : label;
                element.bind('click', function() {
                    $state.go(obj)
                })
            }
        };
    })
    .directive('topicRef', function($state, entityService, $parse) {
        return {
            link: function (scope, element, attrs, modelCtrl) {
                var getter = $parse(attrs.topicRef);
                var topicName = getter(scope);
                if (topicName.hasOwnProperty("name")) topicName = topicName.name;
                if (!topicName) return;
                element.attr('href', "t/"+topicName);
                if (!element[0].innerText) element[0].innerText = topicName;
                element.bind('click', function() {
                    $state.goToTopic(topicName)
                })
            }
        };
    })
    .directive('term', function($api, $state, $compile) {
        return {
            restrict: 'E',
            compile : function(element, attr, linker) {
                return function ($scope, $element, $attr) {
                    var term = $attr.id.replace("<strong>", "").replace("</strong>", "").trim();
                    var primeTerm = $attr.title;
                    var originalContent = $element.html();
                    var expanded;
                    var hasShortDescription = $attr.hasShortDescription;
//                    $attr.title= "eee";
                    $element.bind('click', function(e) {
                        var more = e.target.tagName == "A";
                        var moreAfterPrimeTerm = e.target.id == "+";

                        if (more && !moreAfterPrimeTerm) {
                            window.open(term, '_blank');
                            //$state.goToTerm(term);
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
    .directive('uri', function(entityService) {
        return {
            restrict: 'E',
            compile : function(element, attr, linker) {
                return function ($scope, $element, $attr) {
                    var uri = $element.html();
                    $element.html("<a href='"+getUrl(uri)+"'>"+entityService.getName(uri) + "</a>");
                }
            }
        };
    })
    .directive('topic', function(entityService) {
        return {
            restrict: 'E',
            compile : function(element, attr, linker) {
                return function ($scope, $element, $attr) {
                    var topicName = $element.html();
                    $element.html("<a href='"+getUrl("тема:"+topicName)+"'>"+topicName + "</a>");
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
                    newval = newval.replace(/(?:\r\n|\r|\n)/g, '<br />');
                    newval = newval.replace(new RegExp("\\(([^\\)]+)\\)","gm"), "<bracket>$1</bracket>");
                    element.html(newval);
                    $compile(element.contents())(scope);
                });
            }
        };
    })
    .directive("contributeButton", function($modal, $api) {
        return {
            template: '<i class="icon-star"></i>',
            scope: {
                uri: "="
            },
            link: function(scope, element, attrs) {
                element.bind('click', function(e) {
                    if (!getSelectionText()) {
                        alert("Выберете текст");
                    } else
                        $modal.open({
                            templateUrl: 'contribute-form.html',
                            controller: function ($scope, $modalInstance) {
                                $scope.text = getSelectionText();
                                $scope.quote = function () {
                                    if (!$scope.term) {
                                        alert("Укажите термин");
                                        return;
                                    }
                                    $api.search.quote(scope.uri, $scope.term, $scope.text).then(function () {
                                        $modalInstance.close();
                                    });
                                };
                                $scope.link = function (type) {
                                    if (!$scope.term || !$scope.text) return;
                                    return $api.term.link($scope.term, $scope.text, type).then(function () {
                                        $modalInstance.close();
                                    });
                                }
                            }
                        });
                })
            }
        }
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
    .factory('poster', function($httpParamSerializer) {
        return {
            action: "save",
            method: "POST",
            isArray: false,
            headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
            transformRequest: $httpParamSerializer
        }
    })
    .factory('Video', function($resource, config, poster) {
        poster.interceptor = {responseError: function(response) {
            return response.data;
        }};
        return $resource(config.apiUrl + "resource/video", {}, {
            save: poster,
            get: {
                method: "GET",
                isArray: false,
                url: config.apiUrl + "resource/video/:id"
            }
        });
    })
    .directive('youtube', function($sce) {
        return {
            restrict: 'EA',
            scope: { code:'=' },
            replace: true,
            template: '<div style="height:400px;"><iframe style="overflow:hidden;height:100%;width:100%" width="100%" height="100%" src="{{url}}" frameborder="0" allowfullscreen></iframe></div>',
            link: function (scope) {
                scope.$watch('code', function (newVal) {
                    if (newVal) {
                        scope.url = $sce.trustAsResourceUrl("http://www.youtube.com/embed/" + newVal);
                    }
                });
            }
        };
    })
    .directive('googleDoc', function($sce) {
        return {
            restrict: 'EA',
            scope: { id: '=' },
            replace: true,
            template: '<div style="height:600px;"><iframe src="{{url}}" width="700" height="600" frameborder="0" allowfullscreen></iframe></div>',
            link: function (scope) {
                scope.$watch('id', function (id) {
                    if (id) {
                        scope.url = $sce.trustAsResourceUrl("https://drive.google.com/file/d/"+id+"/preview");
                    }
                });
            }
        };
    })
    .directive("iiHeader", function($rootScope, focus, $state, $timeout) {
        return {
            scope: {},
            templateUrl: "static/partials/header.html",
            link: function(scope, element, attrs) {
                scope.visible = true;
                scope.expand = function() {
                    scope.expanded = true;
                    focus('search-input');
                    scope.query = getSelectedText();
                };
                scope.search = function() {
                    scope.$suggestionSelected(scope.query);
                    scope.expanded = false;
                };
                scope.closeWithDelay = function() {
                    scope.focused = false;
                    $timeout(function(){
                        if (!scope.focused) scope.expanded = false;
                    }, 5000, true);
                };
                $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams){
                    scope.$root.hideLoop = false;
                    if (toState.name != "home") {
                        scope.visible = true;
                    }
                });
                $rootScope.$on('home-state-entered', function() {
                    scope.$root.hideLoop = true;
                    scope.visible = false;
                });
                $rootScope.$watch('audio', function () {
                    scope.audio = $rootScope.audio;
                });
                scope.playPause = function () {
                    scope.audio.play()
                }
            }
        };
    })
    .directive("topics", function($topicSelector, $api, modal) {
        return {
            scope: { ownerUri: '='},
            templateUrl: "static/partials/topics-directive.html",
            link: function(scope, element, attrs) {
                scope.updateRate = function(topic){
                    $api.topic.updateRate(scope.ownerUri, topic.name, topic.rate).then(getTopics);
                };
                scope.updateComment = function(topic){
                    modal.prompt("Редактирование коментария", topic.comment, "Сохранить").then(function (comment) {
                        $api.topic.updateComment(scope.ownerUri, topic.name, comment).then(getTopics);
                    });
                };
                scope.addTopic = function () {
                    if (!scope.newTopic.name) return;
                    $api.topic.addFor(scope.ownerUri, scope.newTopic.name, scope.newTopic.comment, scope.newTopic.rate)
                        .then(function(topic){
                            scope.newTopic = {};
                            getTopics();
                        });
                };
                scope.removeTopic = function (topic) {
                    modal.confirm("Подтверждение", "Коментарий и оценка будут утеряны. Вы уверены что желаете убрать тему?", "Убрать тему")
                        .then(function () {
                            $api.topic.unlinkUri(scope.ownerUri, topic.uri).then(getTopics);
                        })
                };
                scope.getSuggestions = function (q) {
                    return $api.topic.suggest(q)
                };
                function getTopics() {
                    if (!scope.ownerUri) return;
                    scope.loading = true;
                    $api.topic.getFor(scope.ownerUri).then(function(topics){
                        scope.topics = topics;
                    })['finally'](function () {
                        scope.loading = false;
                    });
                }
                scope.openSelector = function () {
                    $topicSelector.select().then(function (topicName) {
                        scope.newTopic.name = topicName;
                    });
                };
                scope.$watch('ownerUri', getTopics);
                scope.newTopic = {}
            }
        };
    })
    .directive('loading-indicator', function() {
        return {
            template: '<div><img src="static/images/ajax-loader.gif"/>Загрузка...</div>'
        }
    })
    .directive('parents', function(entityService) {
        return {
            template: '<span ng-repeat="parent in parents">' +
            '<a class="btn btn-link" ii-ref="parent">{{parent._label}}</a>{{$last ? "" : "/"}}</span>'
        }
    })
    .directive("videoCard", function () {
        return {
            scope: { video: '='},
            templateUrl: "card-video"
        }
    })
    .directive("documentCard", function () {
        return {
            scope: { doc: '='},
            templateUrl: "card-document"
        }
    })
    .directive("recordCard", function ($rootScope, $topicPrompt, $api, ngAudio, $parse) {
        return {
            scope: { record: '='},
            templateUrl: "record-card",
            link: function(scope, element, attrs) {
                scope.playOrPause = function(record) {
                    if (record.played) {
                        $rootScope.audio.pause();
                        record.played = false;
                        return;
                    }
                    if (scope.$root.currentPlayed) scope.$root.currentPlayed.played = false;
                    var volume = 0.5;
                    if ($rootScope.audio) {
                        volume = $rootScope.audio.volume;
                        $rootScope.audio.stop();
                    }
                    $rootScope.audio = ngAudio.load(record.url);
                    $rootScope.audio.volume = volume;
                    $rootScope.audio.play();
                    record.played = true;
                    scope.$root.currentPlayed = record;
                    document.title = record.name;
                };


                scope.addTopic = function (record) {
                    $topicPrompt.prompt().then(function (topic) {
                        $api.topic.addFor(record.uri, topic).then(function () {
                            if (scope.$parent.$parent.update) scope.$parent.$parent.update();
                        })
                    });
                }
            }
        }
    })
    .run(function($state, entityService, $rootScope, analytics, modal){
        var originStateGo = $state.go;
        $state.go = function(to, params, options) {
            if (to.hasOwnProperty('uri') || angular.isString(to)) {
                var uri = angular.isString(to) ? to : to.uri;
                switch (entityService.getType(uri)) {
                    case "term":
                        originStateGo.bind($state)("term", {name: entityService.getName(uri)});
                        return;
                    case "topic":
                        originStateGo.bind($state)("topic", {name: entityService.getName(uri)});
                        return;
                    case "item":
                        var number = entityService.getName(uri);
                        if (isTom5(number)) return;
                        originStateGo.bind($state)("item", {number: number});
                        return;
                    case "category":
                        originStateGo.bind($state)("category", {name: entityService.getName(uri)});
                        return;
                    case "paragraph":
                        var number = entityService.getName(uri);
                        if (isTom5(number)) return;
                        originStateGo.bind($state)("paragraph", {number: number});
                        return;
                    case "article":
                        originStateGo.bind($state)("article", {id: entityService.getName(uri)});
                        return;
                    case "record":
                        originStateGo.bind($state)("record", {code: entityService.getName(uri)});
                        return;
                    case "video":
                        var id = to.id;
                        if (!id) {
                            id = uri.replace("видео:youtube:", "")
                        }
                        originStateGo.bind($state)("video", {id: id});
                        return;
                    case "document":
                        var id = to.id;
                        if (!id) {
                            id = uri.replace("документ:google:", "")
                        }
                        originStateGo.bind($state)("document", {id: id});
                        return;
                }
            } else {
                originStateGo.bind($state)(to, params, options)
            }
        };
        $state.goToVideo = function(video) {
            originStateGo.bind($state)("video", {id: video.id})
        };
        $state.goToDoc = function(doc) {
            originStateGo.bind($state)("document", {id: doc.id})
        };
        $state.goToMainTopic = function() {
            $state.goToTopic("Методика МИЦИАР");
        };
        $state.goToTopic = function(topicName) {
            originStateGo.bind($state)("topic", {name: topicName})
        };
        $state.goToItem = function(number) {
            originStateGo.bind($state)("item", {number: number})
        };
        $state.goToItemRange = function(from, to) {
            originStateGo.bind($state)("item-range", {from: from, to: to})
        };
        $state.goToTerm = function(name) {
            originStateGo.bind($state)("term", {name: name})
        };
        $state.goToHome = function() {
            originStateGo.bind($state)("home")
        };
        $state.goToCabinet = function() {
            originStateGo.bind($state)("cabinet")
        };

        $rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams){
            analytics.pageview(location.pathname);
        });
        function isTom5(number) {
            if (number.indexOf("5.") == 0) {
                modal.message("", "5 том пока официально не опубликован, по этому его текста нет в системе");
                return true;
            }
        }
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

            return value + (tail || '…');
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
function getUrl(uri) {
    var url = uri.hasOwnProperty("uri") ? uri.uri : uri;
    if (url.indexOf("тема:") == 0) {
        return "t/" + encodeURIComponent(url.replace("тема:", ""))
    }
    url = url.replace("статья:", "a/");
    url = url.replace("категория:параграф:", "");
    url = url.replace("категория:", "c/");
    url = url.replace("ии:термин:", "");
    url = url.replace("ии:пункт:", "");
    url = url.replace("ии:пункты:", "");
    url = url.replace("видео:youtube:", "resource/video/");
    url = url.replace("документ:google:", "d/");
    url = url.replace("запись:", "r/");
    return url;
}

function isItemNumber(s) {
    return s.match("^\\d+\\.\\d+$");
}
function isItemRange(s) {
    return s.match("^\\d+\\.\\d+-\\d+\\.\\d+$");
}
function getSelectedText() {
    if (window.getSelection) {
        return window.getSelection().toString();
    } else if (document.selection) {
        return document.selection.createRange().text;
    }
    return null;
}
function getSelectionText() {
    var text = "";
    if (window.getSelection) {
        text = window.getSelection().toString();
    } else if (document.selection && document.selection.type != "Control") {
        text = document.selection.createRange().text;
    }
    return text;
}