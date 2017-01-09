package org.ayfaar.app.translation;

import org.ayfaar.app.services.GoogleSpreadsheetService;
import org.ayfaar.app.services.topics.TopicService;
import static org.ayfaar.app.utils.GoogleSpreadsheetsUtil.getSheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class TopicTranslation_old {
	private static final String SPREADSHEET_ID = "1LeX6A9Va3VQop7JMHkkvgNpnYgnPyCgajPbEm2de2C8";
	private static final String RANGE = "Topic";

	@Autowired
	private TopicService topicService;
	@Autowired
	private GoogleSpreadsheetService googleSpreadsheetService;

	public void transferAllTopicsFromDbToGooglesheet() throws IOException {
		googleSpreadsheetService.setSpreadsheetId(SPREADSHEET_ID);
		googleSpreadsheetService.setRange(RANGE);

		List<String> topicNames = topicService.getAllNames();
		System.out.println("topic count: " + topicNames.size());

		List<List<Object>> values = fromRowToColumn(new ArrayList<>(topicNames));
		Integer updatedRows = googleSpreadsheetService.write(getSheetsService(), values);
		System.out.println("Updated rows: " + updatedRows);
	}

	private List<List<Object>> fromRowToColumn(List<Object> row) {
		List<List<Object>> result = new ArrayList<>();

		for (Object value : row) {
			result.add(Arrays.asList(value));
		}

		result.forEach(System.out::println);

		return result;
	}

	public void transferAllTopicsFromGooglesheetToDb() throws IOException {
		// TODO
	}
}
