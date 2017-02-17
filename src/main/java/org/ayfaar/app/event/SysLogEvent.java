package org.ayfaar.app.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.logging.LogLevel;

@Getter
@Setter
public class SysLogEvent {
    private Object source;
    private String message;
    private LogLevel level;

    public SysLogEvent(Object source, String message, LogLevel level) {
        this.source = source;
        this.message = message;
        this.level = level;
    }
}
