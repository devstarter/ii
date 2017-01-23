package org.ayfaar.app.services;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter @Setter
@NoArgsConstructor
@Component
public class GoogleSpreadsheetService {
	private static final String VALUE_INPUT_OPTION = "RAW";

	private String spreadsheetId;
	private String range;

	public List<List<Object>> read(Sheets service) throws IOException {
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

	public Integer write(Sheets service, Map<Integer, List<Object>> batchData) throws IOException {
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

	public void clear(Sheets service) throws IOException {
		service.spreadsheets().values().clear(spreadsheetId, range, new ClearValuesRequest()).execute();
	}
}
