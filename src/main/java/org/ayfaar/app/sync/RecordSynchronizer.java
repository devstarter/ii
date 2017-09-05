package org.ayfaar.app.sync;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.ayfaar.app.event.RecordRenamedEvent;
import org.ayfaar.app.model.Record;
import org.ayfaar.app.model.VideoResource;
import org.ayfaar.app.services.GoogleSpreadsheetService;
import org.ayfaar.app.services.record.RecordService;
import org.ayfaar.app.services.videoResource.VideoResourceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * 1. Initial uploading
 * 1.1. Get all record names
 * 1.2. Put names to spreadsheet
 *  код ответа | упрощённое название | название для опытных | описание
 * 2. Synchronization
 * 2.1. Get all data from spreadsheet
 * 2.2. Get all records data (names and descriptions)
 * 2.3. On conflicts override local data by spreadsheet
 * 2.4. All local changes translate to the spreadsheet
 */
@Service
@EnableScheduling
@Slf4j
@Profile("!dev")
public class RecordSynchronizer {
    private final RecordService recordService;
    private final VideoResourceService videoResourceService;

    private final GoogleSpreadsheetSynchronizer<RecordSyncData> synchronizer;

    @Inject
    public RecordSynchronizer(GoogleSpreadsheetService spreadsheetService,
                              RecordService recordService,
                              VideoResourceService videoResourceService,
                              @Value("${sync.records.spreadsheet-id}") String spreadsheetId) {
        this.recordService = recordService;
        this.videoResourceService = videoResourceService;
        
        synchronizer = GoogleSpreadsheetSynchronizer.<RecordSyncData>build(spreadsheetService, spreadsheetId)
                .keyGetter(RecordSyncData::code)
                .skipFirstRow()
//                .direction(TWO_WAY)
                .columnUpdater(2, this::updateSimpleName)
                .columnUpdater(3, this::updateOriginalName)
                .columnUpdater(4, this::updateDescription)
                .localDataLoader(this::dataLoader)
                .build();
    }

    private Collection<RecordSyncData> dataLoader() {
        final List<Record> records = recordService.getAll();
        final List<VideoResource> videos = videoResourceService.getAll();

        // create data based on records
        final List<RecordSyncData> syncData = StreamEx.of(records)
                .map(record -> RecordSyncData.builder()
                        .code(record.getCode())
                        .originalName(record.getName())
                        .description(record.getDescription())
                        .build())
                .toList();

        // add data based on videos
        videos.stream().filter(v -> !isEmpty(v.getCode())).forEach(v -> {
            final Optional<RecordSyncData> match = syncData.stream().filter(i -> Objects.equals(i.code, v.getCode())).findFirst();
            if (match.isPresent()) {
                match.get().simpleName(v.getTitle());
            } else {
                syncData.add(RecordSyncData.builder()
                        .code(v.getCode())
                        .simpleName(v.getTitle())
                        .build());

            }
        });
        return syncData;
    }

    @Scheduled(cron = "0 0 2 * * ?") // at 2 AM every day
    public void synchronize() throws IOException {
        synchronizer.sync();
    }

    @Async
    @EventListener
    public void onRecordRenamed(RecordRenamedEvent event) throws IOException {
        final Record record = event.record;
        synchronizer.send(record.getCode(), 3, record.getName());
    }

    private void updateSimpleName(String code, String name) {
        log.info("updateSimpleName: update request for code: {}, new name: {}", code, name);
    }

    private void updateOriginalName(String code, String name) {
        log.info("updateOriginalName: update request for code: {}, new name: {}", code, name);
        recordService.getByCode(code).ifPresent(record -> {
            record.setName(name);
            recordService.save(record);
        });
    }

    private void updateDescription(String code, String description) {
        log.info("updateDescription: update request for code: {}, new description: {}", code, description);
        recordService.getByCode(code).ifPresent(record -> {
            record.setDescription(description);
            recordService.save(record);
        });
    }

    @Getter @Setter
    @Accessors(fluent = true)
    @Builder
    @ToString
    private static class RecordSyncData implements SyncItem {
        public String code;

        public String simpleName;

        public String originalName;

        public String description;

        public List<Object> toRaw() {
            final ArrayList<Object> obj = new ArrayList<>();
            obj.add(code);
            obj.add(simpleName != null ? simpleName : "");
            obj.add(originalName != null ? originalName : "");
            obj.add(description);
            return obj;
        }
    }
}
