package org.ayfaar.app.event;

import org.ayfaar.app.model.Record;

public class RecordRenamedEvent {
    public final Record record;

    public RecordRenamedEvent(Record record) {
        this.record = record;
    }
}
