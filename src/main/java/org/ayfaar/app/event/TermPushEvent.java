package org.ayfaar.app.event;


import lombok.Getter;

public class TermPushEvent extends LinkPushEvent{
    @Getter
    protected String name;

    public TermPushEvent(String name) {
        super();
        this.name = name;
    }
}
