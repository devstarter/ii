package org.ayfaar.app.services;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GoogleSpreadsheetServiceTest {
	@InjectMocks
	private GoogleSpreadsheetService googleSpreadsheetService;

	private static final String SPREADSHEET_ID = "1LeX6A9Va3VQop7JMHkkvgNpnYgnPyCgajPbEm2de2C8";
	private static final String RANGE = "Data";

	@Before
	public void setUp() {
		googleSpreadsheetService.setSpreadsheetId(SPREADSHEET_ID);
		googleSpreadsheetService.setRange(RANGE);
	}

	@Test
	public void testRead() throws Exception {
		Sheets sheetsMock = mock(Sheets.class);
		Sheets.Spreadsheets spreadsheetsMock = mock(Sheets.Spreadsheets.class);
		Sheets.Spreadsheets.Values valuesMock = mock(Sheets.Spreadsheets.Values.class);
		Sheets.Spreadsheets.Values.Get getMock = mock(Sheets.Spreadsheets.Values.Get.class);

		when(sheetsMock.spreadsheets()).thenReturn(spreadsheetsMock);
		when(spreadsheetsMock.values()).thenReturn(valuesMock);
		when(valuesMock.get(anyString(), anyString())).thenReturn(getMock);
		when(getMock.execute()).thenReturn(new ValueRange());

		googleSpreadsheetService.read(sheetsMock);

		verify(valuesMock, times(1)).get(eq(SPREADSHEET_ID), eq(RANGE));
	}

	@Test
	public void testWrite() throws Exception {
		Sheets sheetsMock = mock(Sheets.class);
		Sheets.Spreadsheets spreadsheetsMock = mock(Sheets.Spreadsheets.class);
		Sheets.Spreadsheets.Values valuesMock = mock(Sheets.Spreadsheets.Values.class);
		Sheets.Spreadsheets.Values.Update updateMock = mock(Sheets.Spreadsheets.Values.Update.class);

		when(sheetsMock.spreadsheets()).thenReturn(spreadsheetsMock);
		when(spreadsheetsMock.values()).thenReturn(valuesMock);
		when(valuesMock.update(anyString(), anyString(), any())).thenReturn(updateMock);
		when(updateMock.execute()).thenReturn(new UpdateValuesResponse());
		when(updateMock.setValueInputOption(anyString())).thenReturn(updateMock);

		List<List<Object>> values = new ArrayList<>();
		values.add(Arrays.asList(""));

		googleSpreadsheetService.write(sheetsMock, values);

		ValueRange valueRange = new ValueRange();
		valueRange.setValues(values);
		verify(valuesMock, times(1)).update(eq(SPREADSHEET_ID), eq(RANGE), eq(valueRange));
	}
}


// TODO This is integration test
//public class GoogleSpreadsheetServiceTest {
//	private static final String SPREADSHEET_ID = "1LeX6A9Va3VQop7JMHkkvgNpnYgnPyCgajPbEm2de2C8";
//	private static final String RANGE = "Data";
//	private static final String RANGE_THIRD_LINE = RANGE + "!A3";
//
//	@Test
//	public void testRead() throws Exception {
//		List<List<Object>> values = new GoogleSpreadsheetService(SPREADSHEET_ID, RANGE).read(getSheetsService());
//		System.out.println("row count: " + values.size());
//		values.forEach(row -> System.out.println("column count: " + row.size() + " | " + row));
//
//		assertNotNull("Read spreadsheet shouldn't return null", values);
//		if (values != null) {
//			assertThat(values.size(), greaterThan(0));
//		}
//	}
//
//	@Test
//	public void testWrite() throws Exception {
//		List<List<Object>> values = new ArrayList<>();
//		values.add(Arrays.asList("Topic original language", "Topic translation"));
//
//		assertTrue("Write spreadsheet should return true", new GoogleSpreadsheetService(RANGE_THIRD_LINE, RANGE)
//				.write(getSheetsService(), values));
//	}
//}