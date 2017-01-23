package org.ayfaar.app.services;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GoogleSpreadsheetServiceTest {
	@InjectMocks
	private GoogleSpreadsheetService googleSpreadsheetService;

	private static final String SPREADSHEET_ID = "1LeX6A9Va3VQop7JMHkkvgNpnYgnPyCgajPbEm2de2C8";
	private static final String RANGE = "A:B";

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
}
