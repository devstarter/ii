function DocumentController($scope, $stateParams, $api, messager, $state) {
    if ($stateParams.id) {
        $scope.docLoading = true;
        $api.document.get($stateParams.id).then(function(doc){
            $scope.docLoading = false;
            if (doc.id) {
                $scope.doc = doc;
            } else {
                $scope.showUrlInput = true;
            }
        }, function(response){
            $scope.docLoading = false;
            messager.error("Ошибка добавления документа");
        });
    } else {
        $scope.showUrlInput = true;
        $api.document.last().then(function (list) {
            $scope.last = list;
        })
    }
    $scope.add = function(){
        $scope.docLoading = true;
        $api.document.add($scope.url).then(function(doc){
            $state.goToDoc(doc);
        });
    };
    $scope.last = [];
}
function TopicController($scope, $stateParams, $api, $state, modal, $topicPrompt, messager) {
    $scope.name = $stateParams.name;
    document.title = $scope.name;
    $scope.loading = true;

    function load() {
        $api.topic.get($scope.name, true).then(function(topic){
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
        $topicPrompt.prompt().then(function (topic) {
            $api.topic.addChild(topic, $scope.name).then(load)
        });
    };
    $scope.addChild = function () {
        $topicPrompt.prompt().then(function (topic) {
            $api.topic.addChild($scope.name, topic).then(load)
        });
    };
    $scope.addVideoResource = function () {
        $state.goToVideo("")
    };
    $scope.merge = function () {
        $topicPrompt.prompt().then(function (topic) {
            modal.confirm("Подтвердите объединение тем", "Текущая тема \""+$scope.name+"\" будет удалена из системы, а всё что с ней связанно будет перенесено в выбранную тему (\""+topic+"\"). Подтвержаете объединение?", "Объединить")
                .then(function () {
                    $api.topic.merge($scope.name, topic).then(function () {
                        $state.gotToTopic(topic);
                        messager.ok("Объединение успешно выполнено")
                    })
                })
        })
    };
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
    $scope.number = $stateParams.number.trim();
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
function ResourcesController($scope, $stateParams, $state, Video, Topic, errorService, $api, $modal, $topicSelector) {
    $scope.$root.hideLoop = true;
    $scope.topics = [];
    $scope.newTopic = {};
    $scope.lastVideos = [];

    if ($stateParams.id) {
        $scope.videoLoading = true;
        Video.get({id: $stateParams.id}, function(video){
            $scope.videoLoading = false;
            if (video.id) {
                $scope.video = video;
            } else {
                $scope.showUrlInput = true;
            }
        }, function(response){
            $scope.videoLoading = false;
            errorService.resolve("Ошибка добавления видео: " + response.error);
        });
    } else {
        $scope.showUrlInput = true;
        getLast();
    }

    $scope.save = function(){
        $scope.videoLoading = true;
        Video.save({url: $scope.url}).$promise.then(function(video){
            $state.goToVideo(video);
        });
    };
    $scope.getMore = function () {
        getLast();
    };

    function getLast() {
        $scope.lastLoading = true;
        $api.resource.video.last(Math.ceil($scope.lastVideos.length / 8) + 1).then(function (lastVideos) {
            if (!lastVideos.length) {
                $scope.lastNoMore = true;
                return
            }
            $scope.lastVideos.append(lastVideos);
            var grouped = {};
            angular.forEach($scope.lastVideos, function (v) {
                var d = new Date(v.created_at);
                var diff = Date.now() - d;
                var header;
                if (diff < 24*60*60000) {
                    header = "Добавленные за последние сутки"
                } else if (diff < 7*24*60*60000) {
                    header = "Добавленные за последнюю неделю"
                } else if (diff < 30*7*24*60*60000) {
                    header = "Добавленные за последний месяц"
                } else {
                    header = "Добавленные раньше чем за месяц"
                }
                if (!grouped[header]) grouped[header] = [];
                grouped[header].push(v)
            });
            $scope.last = grouped;
            $scope.lastLoading = false;
        })   
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


