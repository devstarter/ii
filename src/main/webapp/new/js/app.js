angular.module('app', ['ui.router'])
    .config(function($locationProvider, $urlRouterProvider, $stateProvider) {
//        $locationProvider.html5Mode(true).hashPrefix('!');

        $urlRouterProvider.otherwise("/home");
        //
//        // Now set up the states
        $stateProvider
            .state('home', {
                url: "/home",
                templateUrl: "partials/home.html"
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
                templateUrl: "partials/term.html"
//                onEnter: function($location, $stateParams, $log){
//                    $log.info($stateParams)
//                }
            })
    });

Array.prototype.append = function(array){
    this.push.apply(this, array)
};
