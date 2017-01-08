package org.ayfaar.app.services;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Getter @Setter
@NoArgsConstructor
@Component
public class GoogleSpreadsheetService {
	private static final String VALUE_INPUT_OPTION = "RAW";

	private String spreadsheetId;
	private String range;

	public GoogleSpreadsheetService(String spreadsheetId, String range) {
		this.spreadsheetId = spreadsheetId;
		this.range = range;
	}

	public List<List<Object>> read(Sheets service) throws IOException {
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
		List<List<Object>> values = response.getValues();
		if (values == null || values.size() == 0) {
			log.debug("No data read from spreadsheet {}, {}", spreadsheetId, range);
		}
		return values;
	}

	public UpdateValuesResponse write(Sheets service, List<List<Object>> values) throws IOException {
		ValueRange response = new ValueRange();
		response.setValues(values);
		return service.spreadsheets().values().update(spreadsheetId, range, response)
				.setValueInputOption(VALUE_INPUT_OPTION).execute();
	}
}
