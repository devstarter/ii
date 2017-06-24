package org.ayfaar.app.sync;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.ayfaar.app.services.GoogleSpreadsheetService;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.ayfaar.app.sync.GoogleSpreadsheetSynchronizer.SyncDirection.TWO_WAY;

public class GoogleSynchronizerTest {

    private GoogleSpreadsheetSynchronizer<RecordSyncData> synchronizer;

    @Before
    public void init() throws IOException {
        GoogleSpreadsheetService spreadsheetService = new GoogleSpreadsheetService();
        String spreadsheetId = "1hdup3Gm9FWWSvicDx11CGlKigd4UE2hFnJ-zObegn6Y";
        synchronizer = GoogleSpreadsheetSynchronizer.<RecordSyncData>build(spreadsheetService, spreadsheetId)
                .keyGetter(RecordSyncData::code)
                .skipFirstRow()
                .direction(TWO_WAY)
                .columnUpdater(2, this::updateSimpleName)
                .columnUpdater(3, this::updateOriginalName)
                .columnUpdater(4, this::updateDescription)
                .localDataLoader(() -> Collections.singletonList(RecordSyncData.builder()
                        .code("2016-01-01m")
//                        .simpleName("Simple name")
                        .originalName("Original name")
                        .description("Description")
                        .build()))
                .build();
    }

    @Test
    public void test_sync() throws IOException {
        synchronizer.sync();
    }

    @Test
    public void test_send() throws IOException {
        synchronizer.send("2016-01-01m", 2, "New simple name");
    }

    private void updateSimpleName(String code, String name) {

    }

    private void updateOriginalName(String code, String name) {

    }

    private void updateDescription(String code, String name) {

    }

    @Getter
    @Accessors(fluent = true)
    @Builder
    @ToString
    private static class RecordSyncData implements SyncItem {
        public String code;

        public String simpleName;

        public String originalName;

        public String description;

        /*public static RecordSyncData fromRaw(List<Object> objects) {
            return builder()
                    .code((String) objects.get(0))
                    .simpleName((String) objects.get(1))
                    .originalName((String) objects.get(2))
                    .description((String) objects.get(3))
                    .build();
        }*/

        @Override
        public List<Object> toRaw() {
            final ArrayList<Object> obj = new ArrayList<>();
            obj.add(code);
            obj.add(simpleName);
            obj.add(originalName);
            obj.add(description);
            return obj;
        }
    }
}
