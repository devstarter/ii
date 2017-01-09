package org.ayfaar.app.translation;

import com.google.api.services.sheets.v4.Sheets;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.services.GoogleSpreadsheetService;
import org.ayfaar.app.utils.GoogleSpreadsheetsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Component
@Getter @Setter
public class GoogleSpreadsheetTranslator {
	private String baseRange;

	@Value("${spreadsheet-id.translation}")
	private String SPREADSHEET_ID;

	private GoogleSpreadsheetService googleSpreadsheetService;

	@Autowired
	public void setGoogleSpreadsheetService(GoogleSpreadsheetService googleSpreadsheetService) {
		googleSpreadsheetService.setSpreadsheetId(SPREADSHEET_ID);
		this.googleSpreadsheetService = googleSpreadsheetService;
	}

	public Stream<TranslationItem> read() {
		googleSpreadsheetService.setRange(baseRange);

		List<List<Object>> values = new ArrayList<>();
		try {
			 values = googleSpreadsheetService.read(getSheetsService());
		} catch (IOException e) {
			log.error("Can't read translation from spreadsheet with id {} range {}", SPREADSHEET_ID, baseRange, e);
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
		googleSpreadsheetService.setRange(baseRange);

		List<List<Object>> values = new ArrayList<>();
		items.forEach(item -> values.add(Arrays.asList(item.getOrigin(), item.getTranslation())));

		Integer updatedRows = -1;
		try {
			updatedRows = googleSpreadsheetService.write(getSheetsService(), values);
		} catch (IOException e) {
			log.error("Can't write translation to spreadsheet with id {} range {}", SPREADSHEET_ID, baseRange, e);
		}

		return updatedRows;
	}

	public Integer write(TranslationItem item) {
		String fullRange = baseRange + "!" + "A" + item.getRowNumber().get();

		Integer updatedCells = -1;
		try {
			updatedCells = googleSpreadsheetService.write(getSheetsService(),
					Stream.of(item.getOrigin(), item.getTranslation()).collect(Collectors.toList()), fullRange);
		} catch (IOException e) {
			log.error("Can't write translation row to spreadsheet with id {} range {}", SPREADSHEET_ID, fullRange, e);
		}

		return updatedCells;
	}
}
