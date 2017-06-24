package org.ayfaar.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface HasSequence<T extends Sequence> {
    @JsonIgnore
    Class<T> getSequence();
}
