package org.ayfaar.app.services;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.utils.GoogleService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GoogleSpreadsheetService {
	private static final String VALUE_INPUT_OPTION = "RAW";

	public List<List<Object>> read(String spreadsheetId, String range) throws IOException {
		Sheets service = GoogleService.getSheetsService();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
		List<List<Object>> values = response.getValues();
		if (values == null) {
			values = new ArrayList<>();
		}
		if (values.size() == 0) {
			log.debug("No data read from spreadsheet {}, range {}", spreadsheetId, range);
		}

		return values;
	}

	public Integer write(String spreadsheetId, Map<Integer, List<Object>> batchData) throws IOException {
		Sheets service = GoogleService.getSheetsService();
        List<ValueRange> valueRanges = new ArrayList<>();
        batchData.forEach((k, v) -> {
            ValueRange valueRange = new ValueRange();
            valueRange.setRange("A" + k);
            List<List<Object>> data = new ArrayList<>();
            data.add(v);
            valueRange.setValues(data);
            valueRanges.add(valueRange);
        });

        BatchUpdateValuesRequest request = new BatchUpdateValuesRequest();
        request.setValueInputOption(VALUE_INPUT_OPTION);
        request.setData(valueRanges);
        BatchUpdateValuesResponse response = service.spreadsheets().values().batchUpdate(spreadsheetId, request).execute();

        Integer updatedRows = response.getTotalUpdatedRows();
		if (updatedRows == null) {
			updatedRows = 0;
		}
		return updatedRows;
	}

	public Integer write(String spreadsheetId, int index, List<Object> data) throws IOException {
		Sheets service = GoogleService.getSheetsService();

		ValueRange valueRange = new ValueRange();
		valueRange.setValues(Collections.singletonList(data));

        UpdateValuesResponse response = service.spreadsheets().values()
				.update(spreadsheetId, "A" + index, valueRange)
				.setValueInputOption(VALUE_INPUT_OPTION)
				.execute();

        Integer updatedRows = response.getUpdatedRows();
		if (updatedRows == null) {
			updatedRows = 0;
		}
		return updatedRows;
	}

	public void clear(String spreadsheetId, String range) throws IOException {
		Sheets service = GoogleService.getSheetsService();
		service.spreadsheets().values().clear(spreadsheetId, range, new ClearValuesRequest()).execute();
	}
}
