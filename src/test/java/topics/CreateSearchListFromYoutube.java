package topics;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.IntegrationTest;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import com.google.api.services.youtube.YouTube;
import com.google.api.client.json.jackson2.JacksonFactory;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Ignore
public class CreateSearchListFromYoutube extends IntegrationTest{

    @Value("${google.api.key}") private String KEY;
    private static YouTube youtube;

    @Test
    public void searchListFromYoutube() throws IOException {

        YouTube.Builder builder = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {
            }
        });
        builder.setApplicationName("MyApp");
        youtube = builder.build();
        YouTube.Search.List query = youtube.search().list("id,snippet");
        query.setMaxResults(Long.parseLong("50"));
        query.setKey(KEY);
        query.setType("video");
        query.setChannelId("UCbh7Mn8ri5PKx6mOridaHCA");
        query.setFields("items(id/videoId,snippet/title,snippet/description,snippet/thumbnails/default/url),nextPageToken");

        List<String> items = new ArrayList<String>();
        String nextToken = "";
        do {
            query.setPageToken(nextToken);
            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();
            results.stream().forEach(searchResult -> writeEntry(searchResult.getId().getVideoId() +
                    "; http://youtu.be/" + searchResult.getId().getVideoId() +
                    "; " + searchResult.getSnippet().getTitle() + "\n"));
            nextToken = response.getNextPageToken();
        } while (nextToken != null);
    }

    private void writeEntry(String entry) {
        String filename = "text.csv";
        try {
            FileWriter writer = new FileWriter(filename,true);
            BufferedWriter out = new BufferedWriter(writer);
            out.write(entry);
            out.close();

        } catch (Exception e) {
            e.printStackTrace();//oops
        }
    }


}


