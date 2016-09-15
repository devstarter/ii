function KnowledgeBaseController($scope, $state, $api, $q, entityService, auth) {
    $scope.auth = function () {
        auth.authenticate().then(function (user) {
            $scope.user = user;
        });
    };
    $scope.goToCabinet = $state.goToCabinet;

    $api.topic.last(3).then(function (topics) {
        $scope.topics = topics;
    });
    $api.resource.video.last(0, 3).then(function (videos) {
        $scope.videos = videos;
    });
    $api.record.last(0, 3).then(function (records) {
        $scope.records = records;
    });
    $api.document.last(0, 3).then(function (docs) {
        $scope.docs = docs;
    })
}

function RecordController($scope, $stateParams, $api, messager, modal, audioPlayer) {
    $scope.recordLoading = true;
    $scope.last = [];
    $scope.nameFilter = $stateParams.code;

    load();

    $scope.rename = function (record) {
        modal.prompt("Переименование ответа", record.name, "Переименовать").then(function (name) {
            $api.record.rename(record.code, name).then(load)
        })
    };
    $scope.playOrPause = audioPlayer.playOrPause;

    $scope.getMore = function () {
        load(true);
    };

    function load(next) {
        $scope.recordLoading = true;
        $scope.singleMode = false;
        if (!next) {
            $scope.last = [];
            $scope.lastNoMore = false;
        }
        $api.record.get(next ? Math.ceil($scope.last.length / 10) : 0, $scope.nameFilter, $scope.yearFilter).then(function (records) {
            $scope.recordLoading = false;
            if (!records.length && next) {
                $scope.lastNoMore = true;
                return
            }
            $scope.last.append(records);
            $scope.singleMode = records.length == 1;
            $scope.record = $scope.singleMode ? records[0] : null;
            window.title = $scope.singleMode ? records[0].name : "Аудио ответы"
        }, function(response){
            $scope.recordLoading = false;
            messager.error("Ошибка загрузки ответа");
        })
    }

    $scope.update = function () {
        load()
    };

}
function DocumentController($scope, $stateParams, $api, messager, $state) {
    if ($stateParams.id) {
        $scope.docLoading = true;
        $api.document.get($stateParams.id).then(function(doc){
            $scope.docLoading = false;
            if (doc.id) {
                $scope.doc = doc;
                document.title = doc.name;
            } else {
                $scope.showUrlInput = true;
            }
        }, function(response){
            $scope.docLoading = false;
            messager.error("Ошибка загрузки документа");
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
function ImageController($scope, $stateParams, $api, messager, $state) {
    if ($stateParams.id) {
        $scope.imgLoading = true;
        $api.picture.get($stateParams.id).then(function(img){
            $scope.imgLoading = false;
            if (img.id) {
                $scope.img = img;
                picture.title = img.name;
            } else {
                $scope.showUrlInput = true;
            }
        }, function(response){
            $scope.imgLoading = false;
            messager.error("Ошибка загрузки изображения");
        });
    } else {
        $scope.showUrlInput = true;
        $api.picture.last().then(function (list) {
            $scope.last = list;
        })
    }
    $scope.add = function(){
        $scope.imgLoading = true;
        $api.picture.add($scope.url).then(function(img){
            $state.goToImg(img);
        });
    };
    $scope.last = [];
}
function TopicController($scope, $stateParams, $api, $state, modal, $topicPrompt, messager, $timeout, ngAudio, $rootScope) {
    $scope.name = $stateParams.name;
    document.title = $scope.name;

    function load() {
    $scope.loading = true;
        $api.topic.get($scope.name, true).then(function(topic){
            copyObjectTo(topic, $scope);
            document.title = $scope.name;
        }, function () {
            $state.goToMainTopic();
        })['finally'](function () {
            $scope.loading = false;
        });
    }
    $timeout(load);
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
    $scope.addDocResource = function () {
        $state.goToDoc("")
    };
    $scope.merge = function () {
        $topicPrompt.prompt().then(function (topic) {
            modal.confirm("Подтвердите объединение тем", "Текущая тема \""+$scope.name+"\" будет удалена из системы, а всё что с ней связанно будет перенесено в выбранную тему (\""+topic+"\"). Подтвержаете объединение?", "Объединить")
                .then(function () {
                    $api.topic.merge($scope.name, topic).then(function () {
                        $state.goToTopic(topic);
                        messager.ok("Объединение выполнено")
                    })
                })
        })
    };
    $scope.play = function(record) {
        if ($scope.currentPlayed) $scope.currentPlayed.played = false;
        var volume = 0.5;
        if ($rootScope.audio) {
            volume = $rootScope.audio.volume;
            $rootScope.audio.stop();
        }
        $rootScope.audio = ngAudio.load(record.resource.audio_url);
        $rootScope.audio.volume = volume;
        $rootScope.audio.play();
        record.played = true;
        $scope.currentPlayed = record;
    };
    $rootScope.$watch('audio.paused', function () {
        if ($scope.currentPlayed) $scope.currentPlayed.played = !$rootScope.audio.paused;
    })
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
function HomeController($scope, $state, auth) {
    $scope.search = function(query) {
        if (query) {
            $state.goToTerm(query);
        }
    };
    $scope.auth = function () {
        auth.authenticate().then(function (user) {
            $scope.user = user;
        });
    };
    $scope.goToCabinet = $state.goToCabinet;
}
function ItemController($scope, $stateParams, $state, $api, modal) {
    $scope.number = $stateParams.number.trim();
    if (!$scope.number) {
        $state.goToHome();
        return
    }
    if ($scope.number.indexOf("5.") == 0) {
        modal.message("", "5 том пока официально не опубликован, по этому его текста нет в системе");
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

    $api.category.get($scope.number)
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
function ResourcesController($scope, $stateParams, $state, Video, errorService, $api, $timeout, $pager) {
    $scope.topics = [];
    $scope.newTopic = {};
    $scope.last = [];
    document.title = "Последние видео ответы";
    var pager = $pager.createGroupedByDate($api.resource.video.last, "created_at", 6);

    if ($stateParams.id) {
        $scope.videoLoading = true;
        $timeout(Video.get({id: $stateParams.id}, function(video){
            $scope.videoLoading = false;
            if (video.id) {
                $scope.video = video;
                document.title = video.title;
            } else {
                $scope.showUrlInput = true;
            }
        }, function(response){
            $scope.videoLoading = false;
            errorService.resolve("Ошибка добавления видео: " + response.error);
        }));
    } else {
        $scope.showUrlInput = true;
        getLast();
    }

    $scope.save = function(){
        $scope.videoLoading = true;
        $api.resource.video.add($scope.url).then(function(video){
            $state.goToVideo(video);
        });
    };
    $scope.getMore = function () {
        getLast();
    };

    function getLast() {
        $scope.lastLoading = true;
        pager.loadNext().then(function (data) {
            $scope.last = data.grouped;
            $scope.lastLoading = false;
            $scope.lastNoMore = data.last;
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

function CabinetController($scope, $api, $rootScope, auth, modal, $pager) {
    window.title = "Личный кабинет";
    var pager = $pager.createGroupedByDate($api.moderation.lastActions, "created_at", 10);

    if (!auth.isAuthenticated()) auth.authenticate().then(onAuthenticated);
    else onAuthenticated();

    function onAuthenticated() {
        $scope.user = $rootScope.user;
        loadStatus();
        $scope.confirm = function (id) {
            $api.moderation.confirm(id).then(loadStatus);
        };
        $scope.cancel = function (id) {
            modal.confirm("Подтверждение", "Вы уверены что желаете удалить это предложение?", "Удалить").then(function () {
                $api.moderation.cancel(id).then(loadStatus);
            });
        }
    }
    var firstAction;
    function loadStatus() {
        $api.moderation.pendingActions().then(function (pendingActions) {
            $scope.pendingActions = pendingActions;
        });
        loadNextLastActions();
    }
    $scope.updateName = function() {
        modal.prompt("Изменение имени", $scope.user.name, "Изменить").then(function (name) {
            $api.user.rename(name).then(function () {
                $scope.user.name = name;
            });
        });
    };
    $scope.hideActions = function () {
        $api.user.hideActionsBefore(firstAction.id).then(loadStatus)
    };

    $scope.loadMoreLastActions = loadNextLastActions;

    function loadNextLastActions() {
        pager.loadNext().then(function (data) {
            if (data.ungroupedList && data.ungroupedList.length) firstAction = data.ungroupedList[0];
            $scope.lastActions = data.grouped;
            $scope.hasActions = data.ungroupedList && data.ungroupedList.length;
            $scope.hasMoreActions = !data.last;
        });
    }
}


