package org.ayfaar.app.services.moderation;

import java.util.ArrayList;
import java.util.List;

import static org.ayfaar.app.services.moderation.AccessLevel.ADMIN;
import static org.ayfaar.app.services.moderation.AccessLevel.EDITOR;

public enum Action {
    TOPIC (EDITOR),
    TOPIC_CREATE        (TOPIC, ADMIN),
    TOPIC_ADD_CHILD     (TOPIC, ADMIN),
    TOPIC_LINK_RESOURCE (TOPIC),
    TOPIC_RESOURCE_LINK_UPDATE(TOPIC),

    ITEMS_RANGE (EDITOR),
    ITEMS_RANGE_CREATE  (ITEMS_RANGE),
    ITEMS_RANGE_UPDATE  (ITEMS_RANGE),
    ;

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

    public AccessLevel getRequiredAccessLevel() {
        return requiredAccessLevel != null ? requiredAccessLevel : parent.getRequiredAccessLevel();
    }

    @Override
    public String toString() {
        return name()+" with access "+getRequiredAccessLevel().name();
    }
}
