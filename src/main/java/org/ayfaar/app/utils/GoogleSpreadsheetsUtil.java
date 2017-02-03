package org.ayfaar.app.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Slf4j
public class GoogleSpreadsheetsUtil {
	/** Application name. */
	private static final String APPLICATION_NAME = "Spreadsheets ii";

    private static final String ACCOUNT_PRIVATE_KEY = "/account-private-key-google-api-devstarter.json";

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the scopes. */
    private static final Set<String> SCOPES = Collections.singleton(SheetsScopes.SPREADSHEETS);

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;
	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (Throwable t) {
			log.error(t.getMessage(), t);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	private static Credential authorize() throws IOException {
        GoogleCredential credential = null;
        try {
            credential = GoogleCredential
                    .fromStream(GoogleSpreadsheetsUtil.class.getResourceAsStream(ACCOUNT_PRIVATE_KEY))
                    .createScoped(SCOPES);
        } catch (IOException e) {
            log.error("Can't create Google credential", e);
        }

        return credential;
	}

	/**
	 * Build and return an authorized Sheets API client service.
	 * @return an authorized Sheets API client service
	 * @throws IOException
	 */
	public static Sheets getSheetsService() throws IOException {
		Credential credential = authorize();
		return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}
}
