function TopicController($scope, $stateParams, $api, $state, $modal) {
    $scope.name = $stateParams.name;
    document.title = $scope.name;
    $scope.loading = true;

    function load() {
        $api.topic.get($scope.name).then(function(topic){
            $scope.loading = false;
            copyObjectTo(topic, $scope);
            document.title = $scope.name;
        });
    }
    load();
    $scope.unlink = function (linkedTopic) {
        $api.topic.unlink($scope.name, linkedTopic).then(load);
    };
    $scope.addParent = function () {
        var parentScope = $scope;
        $modal.open({
            templateUrl: 'static/partials/topic-select.html',
            controller: function ($scope, $modalInstance) {
                $scope.suggestTopics = function (q) {
                    return $api.topic.suggest(q);
                };
                $scope.select = function() {
                    $api.topic.addChild($scope.topic, parentScope.name).then(function() {
                        $modalInstance.close();
                        load();
                    })
                };
            }
        });
    };
    $scope.addChild = function () {
        var parentScope = $scope;
        $modal.open({
            templateUrl: 'static/partials/topic-select.html',
            controller: function ($scope, $modalInstance) {
                $scope.suggestTopics = function (q) {
                    return $api.topic.suggest(q);
                };
                $scope.select = function() {
                    $api.topic.addChild(parentScope.name, $scope.topic).then(function() {
                        $modalInstance.close();
                        load();
                    })
                };
            }
        });
    }
}
function CategoryController($scope, $stateParams, $api, $state) {

    $scope.name = $stateParams.name;
    document.title = $scope.name;
    $scope.loading = true;

    $api.category.get($scope.name).then(function(category){
        $scope.loading = false;
        copyObjectTo(category, $scope);
        document.title = $scope.name;
    });
}

function HomeController($scope, $state) {
    $scope.search = function(query) {
        if (query) {
            $state.goToTerm(query);
        }
    };
}


function ItemController($scope, $stateParams, $state, $api) {
    $scope.number = $stateParams.number;
    if (!$scope.number) {
        $state.goToHome();
        return
    }
    document.title = $scope.number;

    $scope.content = "Загрузка...";
    $api.item.get($scope.number).then(function (item) {
        copyObjectTo(item, $scope);
    });
    $scope.goPrev = function() {
        $state.go($scope.previous);
    };
    $scope.goNext = function() {
        $state.go($scope.next);
    }
}

function ItemRangeController($scope, $stateParams, $api) {

    $scope.from = $stateParams.from;
    $scope.to = $stateParams.to;
    document.title = "Абзацы " + $scope.from+" - "+$scope.to;
    $scope.loading = true;

    $api.item.getRange($scope.from, $scope.to)
        .then(function(items){
            $scope.loading = false;
            $scope.items = items;
        });
}


function ParagraphController($scope, $stateParams, $api, $state) {

    $scope.number = $stateParams.number;
    document.title = "§"+$scope.number;
    $scope.loading = true;
    $scope.goNext = function() {
        $state.go($scope.next);
    };

    $api.category.get("параграф:"+$scope.number)
        .then(function(paragrapg){
            $scope.loading = false;
            copyObjectTo(paragrapg, $scope);
        });
}


function TaggerController($scope, $stateParams, $api) {
    $scope.$root.hideLoop = true;
    $scope.getTags = function(){
        $scope.loading = true;
        $scope.terms = [];
        $api.term.getTermsInText($scope.text).then(function(terms){
            $scope.terms = terms;
            $scope.loading = false;
        });
    };
}

function ResourcesController($scope, $stateParams, $state, Video, Topic, errorService, $api) {
    $scope.$root.hideLoop = true;
    $scope.topics = [];
    $scope.lastTen = [];
    
    if ($stateParams.id) {
        $scope.videoLoading = true;
        Video.get({id: $stateParams.id}, function(video){
            $scope.videoLoading = false;
            if (video.id) {
                $scope.video = video;
                getTopics();
            } else {
                $scope.showUrlInput = true;
            }
        }, function(response){
            $scope.videoLoading = false;
            errorService.resolve("Ошибка добавления видео: " + response.error);
        });
    } else {
        $scope.showUrlInput = true;
        $api.resource.video.lastTen().then(function (lastVideos) {
            $scope.lastTen = lastVideos;
        })
    }
    
    $scope.updateRate = function(topic){
        Topic.rate({forUri: $scope.video.uri, topicUri: topic.uri, rate: topic.rate})
    };

    $scope.save = function(){
        $scope.videoLoading = true;
        Video.save({url: $scope.url}).$promise.then(function(video){
            $state.goToVideo(video);
        });
    };
    
    $scope.addTopic = function () {
        Topic.addForUri({uri: $scope.video.uri, name: $scope.newTopic}).$promise.then(function(topic){
            $scope.newTopic = '';
            getTopics();
        });
    };
    
    $scope.removeTopic = function (topic) {
        Topic.deleteForUri({uri: $scope.video.uri, topicUri: topic.uri}).$promise.then(function(topic){
            getTopics();
        });
    };
    $scope.getSuggestions = function (q) {
        return Topic.suggest({q: q}).$promise
    };
    
    function getTopics() {
        Topic.getForUri({uri: $scope.video.uri}).$promise.then(function(topics){
            $scope.topics = topics;
        });
    }
}

function ArticleController($scope, $stateParams, $state, $api) {
    $scope.id = $stateParams.id;
    if (!$scope.id) {
        $state.goToHome();
        return
    }

    $scope.loading = true;

    $api.article.get($scope.id).then(function (a) {
        $scope.loading = false;
        document.title = a.name;
        copyObjectTo(a, $scope);
    });
}


