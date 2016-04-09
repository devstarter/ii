package org.ayfaar.app.services.moderation;

import java.util.ArrayList;
import java.util.List;

import static org.ayfaar.app.services.moderation.AccessLevel.ROLE_ADMIN;
import static org.ayfaar.app.services.moderation.AccessLevel.ROLE_EDITOR;

public enum Action {
    TOPIC (ROLE_EDITOR),
    TOPIC_CREATE        (TOPIC, ROLE_ADMIN),
    TOPIC_ADD_CHILD     (TOPIC, ROLE_ADMIN),
    TOPIC_LINK_RESOURCE (TOPIC),

    ITEMS_RANGE (ROLE_EDITOR),
    ITEMS_RANGE_CREATE  (ITEMS_RANGE),
    ITEMS_RANGE_UPDATE  (ITEMS_RANGE);

    private Action parent = null;
    private AccessLevel requiredAccessLevel;
    private List<Action> children = new ArrayList<>();


    Action() {
        this(null, null);
    }
    Action(Action parent) {
        this(parent, null);
    }
    Action(AccessLevel requiredAccessLevel) {
        this(null, requiredAccessLevel);
    }
    Action(Action parent, AccessLevel requiredAccessLevel) {
        this.parent = parent;
        this.requiredAccessLevel = requiredAccessLevel;
        if (this.parent != null) {
            this.parent.addChild(this);
        }
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
}
