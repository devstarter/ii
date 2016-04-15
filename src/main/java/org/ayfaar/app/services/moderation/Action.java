package org.ayfaar.app.services.moderation;

import java.util.ArrayList;
import java.util.List;

import static org.ayfaar.app.services.moderation.UserRole.ROLE_ADMIN;
import static org.ayfaar.app.services.moderation.UserRole.ROLE_EDITOR;

public enum Action {
    TOPIC (ROLE_EDITOR),
    TOPIC_CREATE        (TOPIC, ROLE_ADMIN),
    TOPIC_ADD_CHILD     (TOPIC, ROLE_ADMIN),
    TOPIC_LINK_RESOURCE (TOPIC),
    TOPIC_RESOURCE_LINK_UPDATE(TOPIC),

    ITEMS_RANGE (ROLE_EDITOR),
    ITEMS_RANGE_CREATE  (ITEMS_RANGE),
    ITEMS_RANGE_UPDATE  (ITEMS_RANGE),
    ;

    private Action parent = null;
    private UserRole requiredAccessLevel;
    private List<Action> children = new ArrayList<>();


    Action() {
        this(null, null);
    }
    Action(Action parent) {
        this(parent, null);
    }
    Action(UserRole requiredAccessLevel) {
        this(null, requiredAccessLevel);
    }
    Action(Action parent, UserRole requiredAccessLevel) {
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

    public UserRole getRequiredAccessLevel() {
        return requiredAccessLevel != null ? requiredAccessLevel : parent.getRequiredAccessLevel();
    }

    @Override
    public String toString() {
        return name()+" with access "+getRequiredAccessLevel().name();
    }
}
