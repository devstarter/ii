package org.ayfaar.app.sync;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.event.EventPublisher;
import org.ayfaar.app.event.SysLogEvent;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.services.GoogleSpreadsheetService;
import org.ayfaar.app.utils.TermService;
import org.springframework.boot.logging.LogLevel;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;

@Service
@Slf4j
@EnableScheduling
public class VocabularySynchronizer {
    public static final String myName = "Синхронизатор словаря";

    @Inject TermService termService;
    @Inject GoogleSpreadsheetService spreadsheetService;
    @Inject EventPublisher publisher;
    private boolean inProcess = false;

    @Scheduled(cron = "0 0 * * * *") // every hour
    public void synchronize() throws IOException {
        if (inProcess) {
            log.warn("Synchronizer already in process");
            return;
        }
        inProcess = true;
        GoogleSpreadsheetSynchronizer<VocabularySyncItem> synchronizer = GoogleSpreadsheetSynchronizer.<VocabularySyncItem>build(spreadsheetService, "1h3Gy0x1-OvpznGvrugPBf7-Rqr9_MXtbj09AXrV9q2Q")
                .keyGetter(VocabularySyncItem::term)
                .skipFirstRow()
                .localDataLoader(this::getLocalData)
                .columnUpdater(2, this::updateShortDescription)
                .build();
        synchronizer.sync();
        inProcess = false;
    }

    private void updateShortDescription(String termName, String newShortDescription) {
        if (!isEmpty(newShortDescription) && newShortDescription.length() > 255) {
            log.warn("New short description `{}` for term `{}` too long", newShortDescription, termName);
            return;
        }

        final Optional<TermService.TermProvider> termProviderOpt = termService.get(termName);
        if (termProviderOpt.isPresent()) {
            final Term term = termProviderOpt.get().getTerm();
            String oldShortDescription = term.getShortDescription();
            if (isEmpty(oldShortDescription)) oldShortDescription = "<пусто>";
            term.setShortDescription(newShortDescription);
            termService.save(term);
            publisher.publishEvent(new SysLogEvent(myName, String.format("Обновлено короткое описание термина %s. Старый вариант: %s, новый: %s", termName, oldShortDescription, newShortDescription), LogLevel.INFO));
        } else {
            // не возможная пока ситуация
            publisher.publishEvent(new SysLogEvent(myName, "Появился новый термин: " + termName, LogLevel.WARN));
        }
    }

    private Collection<VocabularySyncItem> getLocalData() {
        return termService.getAll().stream()
                .map(Map.Entry::getValue)
                .map(TermService.TermProvider::getMainOrThis)
                .distinct()
                .map(term -> VocabularySyncItem.builder()
                        .term(term.getName())
                        .shortDescription(term.getShortDescription().orElse(null))
                        .build())
                .collect(Collectors.toList());
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    @Builder
    @ToString
    private static class VocabularySyncItem implements SyncItem {
        public String term;
        public String shortDescription;

        @Override
        public List<Object> toRaw() {
            final ArrayList<Object> obj = new ArrayList<>();
            obj.add(term);
            obj.add(shortDescription);
            return obj;
        }
    }
}
