package org.ayfaar.app.event;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.logging.LogLevel;

@Getter
@Setter
@Builder
public class SysLogEvent {
    private String source;
    private String message;
    private LogLevel level;

    public SysLogEvent(Object source, String message, LogLevel level) {
        this.source = source.getClass().getSimpleName();
        this.message = message;
        this.level = level;
    }

}
