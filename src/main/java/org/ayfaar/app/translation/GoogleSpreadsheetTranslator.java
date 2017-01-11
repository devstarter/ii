package org.ayfaar.app.translation;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.services.GoogleSpreadsheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.ayfaar.app.utils.GoogleSpreadsheetsUtil.getSheetsService;

@Slf4j
@Component @Scope("prototype")
public class GoogleSpreadsheetTranslator {
	private String sheetName;

	private GoogleSpreadsheetService googleSpreadsheetService;

	@Autowired
	public GoogleSpreadsheetTranslator(GoogleSpreadsheetService service,
									   @Value("${translation.spreadsheet-id}") String spreadsheetId) {
		service.setSpreadsheetId(spreadsheetId);
		this.googleSpreadsheetService = service;
	}

	public Stream<TranslationItem> read() {
		googleSpreadsheetService.setRange(sheetName);

		List<List<Object>> values = new ArrayList<>();
		try {
			 values = googleSpreadsheetService.read(getSheetsService());
		} catch (IOException e) {
			log.error("Can't read translation from range {}", sheetName, e);
		}

		List<TranslationItem> result = new ArrayList<>();
		for (List<Object> row : values) {
			if (row.isEmpty()) {
				continue;
			}
			TranslationItem translationItem = new TranslationItem();
			translationItem.setRowNumber(Optional.of(values.indexOf(row)+1));
			translationItem.setOrigin((String) row.get(0));
			if (row.size() > 1) {
				translationItem.setTranslation((String) row.get(1));
			}
			result.add(translationItem);
		}

		return result.stream();
	}

	public Integer write(Stream<TranslationItem> items) {
		googleSpreadsheetService.setRange(sheetName);

		List<List<Object>> values = new ArrayList<>();
		items.forEach(item -> values.add(Arrays.asList(item.getOrigin(), item.getTranslation())));

		Integer updatedRows = -1;
		try {
			updatedRows = googleSpreadsheetService.write(getSheetsService(), values);
		} catch (IOException e) {
			log.error("Can't write translation to range {}", sheetName, e);
		}

		return updatedRows;
	}

	public Integer write(TranslationItem item) {
		String fullRange = sheetName + "!" + "A" + item.getRowNumber().get();

		Integer updatedCells = -1;
		try {
			updatedCells = googleSpreadsheetService.write(getSheetsService(),
					Stream.of(item.getOrigin(), item.getTranslation()).collect(Collectors.toList()), fullRange);
		} catch (IOException e) {
			log.error("Can't write translation row to range {}", fullRange, e);
		}

		return updatedCells;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
}
