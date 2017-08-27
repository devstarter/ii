import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.utils.GoogleService;
import org.junit.Test;

import java.io.IOException;


@Slf4j
public class GoogleDocImporter {

    @Test
    public void parse() throws Exception {
        Drive service = GoogleService.getDriveService();

        File file = null;
        try {
            file = service.files().get("1B8Sn3U1zMArXx0iITNwOjGfTMfhK48ipxt7M3UR6K5A").setFields("*").execute();

            System.out.println("Title: " + file.getName());
            System.out.println("Description: " + file.getDescription());
            System.out.println("MIME type: " + file.getMimeType());
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
        }

        /*OutputStream outputStream = new ByteArrayOutputStream();
        service.files().export("1On5RVxKHJdgVktcem27XD32_Rd5j6B-1hekmQK0NeTI", MimeTypeUtils.TEXT_HTML_VALUE).executeAndDownloadTo(outputStream);

        String htmlImput = outputStream.toString();
        String markdown = new Remark().convert(htmlImput);*/
        // com.overzealous.remark.convert.InlineStyle.BOLD_PATTERN
    }

    /*private static InputStream downloadFile(Drive service, File file) {
        if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
            try {
                HttpResponse resp =
                        service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()))
                                .execute();
                return resp.getContent();
            } catch (IOException e) {
                // An error occurred.
                e.printStackTrace();
                return null;
            }
        } else {
            // The file doesn't have any content stored on Drive.
            return null;
        }
    }*/

}
