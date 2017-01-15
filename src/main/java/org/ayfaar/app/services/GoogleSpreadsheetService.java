package org.ayfaar.app.services;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ClearValuesRequest;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
			log.debug("No data read from spreadsheet {}, {}", spreadsheetId, range);
		}

		return values;
	}

	public Integer write(Sheets service, List<List<Object>> rows) throws IOException {
		clear(service);
		ValueRange response = new ValueRange();
		response.setValues(rows);
		UpdateValuesResponse updateValuesResponse = service.spreadsheets().values()
				.update(spreadsheetId, range, response).setValueInputOption(VALUE_INPUT_OPTION).execute();
		Integer updatedRows = updateValuesResponse.getUpdatedRows();
		if (updatedRows == null) {
			updatedRows = 0;
		}
		return updatedRows;
	}

	public Integer write(Sheets service, List<Object> row, String range) throws IOException {
		List<List<Object>> rows = Arrays.asList(row);
		ValueRange response = new ValueRange();
		response.setValues(rows);
		UpdateValuesResponse updateValuesResponse =  service.spreadsheets().values()
				.update(spreadsheetId, range, response).setValueInputOption(VALUE_INPUT_OPTION).execute();
		Integer updatedCells = updateValuesResponse.getUpdatedCells();
		if (updatedCells == null) {
			updatedCells = 0;
		}
		return updatedCells;
	}

	public void clear(Sheets service) throws IOException {
		service.spreadsheets().values().clear(spreadsheetId, range, new ClearValuesRequest()).execute();
	}
}
