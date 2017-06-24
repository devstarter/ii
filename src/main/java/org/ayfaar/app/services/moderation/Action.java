package org.ayfaar.app.services.moderation;

import java.util.ArrayList;
import java.util.List;

import static org.ayfaar.app.services.moderation.UserRole.ROLE_ADMIN;
import static org.ayfaar.app.services.moderation.UserRole.ROLE_AUTHENTICATED;
import static org.ayfaar.app.services.moderation.UserRole.ROLE_EDITOR;

public enum Action {
    TOPIC (ROLE_EDITOR),
    TOPIC_CREATE        ("Создание ключевого слова <topic>{}</topic>", TOPIC, ROLE_ADMIN),
    TOPIC_ADD_CHILD     ("Создание дочернего ключевого слова <topic>{}</topic> для <topic>{}</topic>", TOPIC, ROLE_ADMIN),
    TOPIC_LINK_RESOURCE ("Добавление <uri>{}</uri> к ключевому слову <topic>{}</topic>", TOPIC),
    TOPIC_LINK_RANGE    ("Добавление диапалоза с {} по {} к ключевому слову <topic>{}</topic>", TOPIC),
    TOPIC_RESOURCE_LINK_COMMENT_UPDATE("Обновление комментария в связке между <uri>{}</uri> и ключевым словом <topic>{}</topic>, новый коментарий: `{}` предложен", TOPIC),
    TOPIC_RESOURCE_LINK_RATE_UPDATE("Обновление весового коэфициента в связке между <uri>{}</uri> и ключевым словом <topic>{}</topic>, новые значение: {} предложено", TOPIC),
    TOPIC_RESOURCE_LINK_UPDATE(TOPIC), // for fix db problem
    TOPIC_RESOURCE_LINKED("Ключевое слово <topic>{}</topic> прикреплена к <uri>{}</uri>"),

    ITEMS_RANGE (ROLE_EDITOR),
    ITEMS_RANGE_CREATE  (ITEMS_RANGE),
    ITEMS_RANGE_UPDATE  (ITEMS_RANGE),

    USER_RENAME("Имя пользователя {} измененно на {}"),
    TOPIC_RESOURCE_UNLINKED("От ключевого слова <topic>{}</topic> откреплён <uri>{}</uri>"),
    TOPIC_CHILD_ADDED("Ключевому слову <topic>{}</topic> добавлено дочернее ключевое слово <topic>{}</topic>"),
    TOPIC_TOPIC_UNLINKED("От ключевого слова <topic>{}</topic> откреплено ключевоое слово <topic>{}</topic>"),
    TOPIC_UNLINK_TOPIC("Открепление от ключевого слова <topic>{}</topic> дочернего <topic>{}</topic>", ROLE_ADMIN),
    TOPIC_UNLINK_RESOURCE("Отмена связи между <uri>{}</uri> и ключевым словом <uri>{}</uri>", TOPIC),
    TOPIC_MERGE("Объединение ключевого слова <topic>{}</topic> в ключевое слово <topic>{}</topic>", ROLE_ADMIN),
    TOPIC_MERGED("Ключевое слово `{}` объединена с <topic>{}</topic>"),

    VIDEO_ADDED("Добавлено видео <uri label='{}'>{}</uri>"),
    VIDEO_ADD("Добавление нового видео по ссылке {}", UserRole.ROLE_AUTHENTICATED),
    VIDEO_REMOVE("Удаление видео <uri>видео:youtube:{}</uri>", UserRole.ROLE_EDITOR),
    VIDEO_UPDATE_TITLE("Обновление названия видео <uri>{}</uri> на `{}`", UserRole.ROLE_EDITOR),
    VIDEO_UPDATE_CODE("Задание или изменеие кода видео <uri>видео:youtube:{}</uri>, код `{}`", UserRole.ROLE_EDITOR),
    VIDEO_CODE_UPDATED("Кода видео <uri label='{}'>{}</uri> обновился c `{}` на `{}`", UserRole.ROLE_EDITOR),
    NEW_USER("Выполнен вход в систему новым "),
    VIDEO_REMOVED("Видео `{}` c id: {} далено из системы"),
    RECORD_RENAME("Переименование ответа <uri>запись:{}</uri> на `{}`", ROLE_EDITOR),
    RECORD_RENAMED("Ответ <uri>{}</uri> переименован c `{}` в `{}`"),

    DOCUMENT_CREATED("Добавлен документ <uri label='{}'>{}</uri>"),
    DOCUMENT_RENAME("Переименование документа <uri>{}</uri> на `{}`", ROLE_EDITOR),
    DOCUMENT_RENAMED("Документ переименован c `{}` на <uri label='{}'>{}</uri>"),
    DOCUMENT_ADD("Добавление документа {}", ROLE_AUTHENTICATED),

    SYSLOG_TRANSLATION_NEW("\"{}\" -> \"{}\", "),
    SYSLOG_TRANSLATION_UPDATE("\"{}\" из \"{}\" в \"{}\", "), SYS_EVENT(""),
    QUOTE_CREATED("К термину <uri>ии:термин:{}</uri> добавленна цитата с <uri>{}</uri>", ROLE_EDITOR),
    CREATE_QUOTE("К термину <uri>ии:термин:{}</uri> предлагается цитата `{}` связанная с <uri>{}</uri>", ROLE_EDITOR);

    private Action parent = null;
    private UserRole requiredAccessLevel;
    private List<Action> children = new ArrayList<>();
    public String message;


    Action(Action parent) {
        this(null, parent, null);
    }
    Action(UserRole requiredAccessLevel) {
        this(null, null, requiredAccessLevel);
    }
    Action(String message, UserRole requiredAccessLevel) {
        this(message, null, requiredAccessLevel);
    }
    Action(String message, Action parent, UserRole requiredAccessLevel) {
        this.message = message;
        this.parent = parent;
        this.requiredAccessLevel = requiredAccessLevel;
        if (this.parent != null) {
            this.parent.addChild(this);
        }
    }


    Action(String message) {
        this.message = message;
    }


    Action(String message, Action parent) {
        this(message, parent, null);
    }

    public Action parent() {
        return parent;
    }

    public boolean is(Action other) {
        if (other == null) {
            return false;
        }

        for (Action action = this; action != null; action = action.parent()) {
            if (other == action) {
                return true;
            }
        }
        return false;
    }

    public Action[] children() {
        return children.toArray(new Action[children.size()]);
    }

    public Action[] allChildren() {
        List<Action> list = new ArrayList<>();
        addChildren(this, list);
        return list.toArray(new Action[list.size()]);
    }

    private static void addChildren(Action root, List<Action> list) {
        list.addAll(root.children);
        for (Action child : root.children) {
            addChildren(child, list);
        }
    }

    private void addChild(Action child) {
        this.children.add(child);
    }

    public UserRole getRequiredAccessLevel() {
        return requiredAccessLevel != null ? requiredAccessLevel : parent.getRequiredAccessLevel();
    }

    @Override
    public String toString() {
        return name()+" with access "+getRequiredAccessLevel().name();
    }
}
