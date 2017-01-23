package org.ayfaar.app.translation;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.services.GoogleSpreadsheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.ayfaar.app.utils.GoogleSpreadsheetsUtil.getSheetsService;

@Slf4j
@Component @Scope("prototype")
public class GoogleSpreadsheetTranslator {
    private String range = "A:B";
	private GoogleSpreadsheetService googleSpreadsheetService;

	@Autowired
	public GoogleSpreadsheetTranslator(GoogleSpreadsheetService service,
                                       @Value("${translation.spreadsheet-id}") String spreadsheetId) {
		service.setSpreadsheetId(spreadsheetId);
		this.googleSpreadsheetService = service;
	}

	public Stream<TranslationItem> read() {
        googleSpreadsheetService.setRange(range);

		List<List<Object>> values = new ArrayList<>();
		try {
			 values = googleSpreadsheetService.read(getSheetsService());
		} catch (IOException e) {
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
            updatedRows = googleSpreadsheetService.write(getSheetsService(), batchData);
        } catch (IOException e) {
            log.error("Can't write translations", e);
        }

		return updatedRows;
	}

	public void setRange(String range) {
        this.range = range;
    }
}
