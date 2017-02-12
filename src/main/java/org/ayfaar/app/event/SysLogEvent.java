package org.ayfaar.app.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class SysLogEvent extends ApplicationEvent{
    private String message;

    public SysLogEvent(Object source, String message) {
        super(source);
        this.message = message;
    }
}
