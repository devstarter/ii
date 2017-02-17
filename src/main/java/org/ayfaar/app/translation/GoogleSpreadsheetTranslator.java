package org.ayfaar.app.translation;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.event.EventPublisher;
import org.ayfaar.app.event.SysLogEvent;
import org.ayfaar.app.services.GoogleSpreadsheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class GoogleSpreadsheetTranslator {
	private final String spreadsheetId;
	private final String range = "A:B";
	private final GoogleSpreadsheetService googleSpreadsheetService;
    private final EventPublisher publisher;

	@Autowired
	public GoogleSpreadsheetTranslator(GoogleSpreadsheetService service,
                                       @Value("${translation.spreadsheet-id}") String spreadsheetId,
                                       EventPublisher publisher) {
		this.spreadsheetId = spreadsheetId;
		this.googleSpreadsheetService = service;
        this.publisher = publisher;
	}

	public Stream<TranslationItem> read() {
		List<List<Object>> values = new ArrayList<>();
		try {
			 values = googleSpreadsheetService.read(spreadsheetId, range);
		} catch (IOException e) {
			// dispatch syslog event
			log.error("Can't read translation from range {}", range, e);
            publisher.publishEvent(new SysLogEvent(this, "Can't read translation from range " + range + ". "
                    + e.getMessage(), LogLevel.ERROR));
        }

		List<TranslationItem> result = new ArrayList<>();
		for (List<Object> row : values) {
			if (row.isEmpty()) {
				continue;
			}
			TranslationItem translationItem = new TranslationItem();
			translationItem.setRowNumber(Optional.of(values.lastIndexOf(row) + 1));
			translationItem.setOrigin((String) row.get(0));
			if (row.size() > 1) {
				translationItem.setTranslation((String) row.get(1));
			}
			result.add(translationItem);
		}

		return result.stream();
	}

	public Integer write(Stream<TranslationItem> translationItems) {
        Map<Integer, List<Object>> batchData = new HashMap<>();
        translationItems.forEach(t ->
                        batchData.put(t.getRowNumber().get(), Stream.of(t.getOrigin(), t.getTranslation()).collect(Collectors.toList()))
        );

        Integer updatedRows = -1;
        try {
            updatedRows = googleSpreadsheetService.write(spreadsheetId, batchData);
        } catch (IOException e) {
            // dispatch syslog event
            log.error("Can't write translations", e);
            publisher.publishEvent(new SysLogEvent(this, "Can't write translations. " + e.getMessage(), LogLevel.ERROR));
        }

		return updatedRows;
	}
}
