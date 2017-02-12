package org.ayfaar.app.translation;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.services.GoogleSpreadsheetService;
import org.ayfaar.app.utils.SysLogPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final SysLogPublisher logPublisher;

	@Autowired
	public GoogleSpreadsheetTranslator(GoogleSpreadsheetService service,
                                       @Value("${translation.spreadsheet-id}") String spreadsheetId,
                                       SysLogPublisher logPublisher) {
		this.spreadsheetId = spreadsheetId;
		this.googleSpreadsheetService = service;
        this.logPublisher = logPublisher;
	}

	public Stream<TranslationItem> read() {
		List<List<Object>> values = new ArrayList<>();
		try {
			 values = googleSpreadsheetService.read(spreadsheetId, range);
		} catch (IOException e) {
			// dispatch syslog event
			log.error("Can't read translation from range {}", range, e);
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
            logPublisher.log("Updating google spreadsheet translation table with " + batchData.size() + " rows");
            updatedRows = googleSpreadsheetService.write(spreadsheetId, batchData);
            logPublisher.log("Google spreadsheet translation table updated successfully");
        } catch (IOException e) {
			// dispatch syslog event
            log.error("Can't write translations", e);
            logPublisher.log("Can't write translations. " + e.getMessage());
        }

		return updatedRows;
	}
}
