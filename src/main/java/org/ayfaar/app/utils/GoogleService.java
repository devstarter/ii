package org.ayfaar.app.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class GoogleService {
    @Value("${google.api.key}") private String API_KEY;
    @Value("${drive-dir}") private String driveDir;

    private static final String APPLICATION_NAME = "ii";
    private static HttpTransport httpTransport;
    private static FileDataStoreFactory dataStoreFactory;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static Drive drive;

    private final ResourceLoader resourceLoader;
    private final RestTemplate restTemplate;

    private final String DocOrImageInfoUrl = "https://www.googleapis.com/drive/v2/files/{id}?key={key}";
    private final String forGetCodeOfVideoUrl = "https://www.googleapis.com/youtube/v3/videos?key={API_KEY}&fields=items(snippet(tags))&part=snippet&id={video_id}";
    public static final String codeVideoPatternRegExp = "^\\d{4}-\\d{2}-\\d{2}(_\\d{1,2})?([a-z])?([-_][km])?$";

    @Inject
    public GoogleService(ResourceLoader resourceLoader, RestTemplate restTemplate) {
        this.resourceLoader = resourceLoader;
        this.restTemplate = restTemplate;
    }

    public VideoInfo getVideoInfo(String id) {
        final Map response = restTemplate.getForObject("https://content.googleapis.com/youtube/v3/videos?part={part}&id={id}&key={key}",
                Map.class, "snippet", id, API_KEY);
        //noinspection unchecked
        final List<Map> items = (List<Map>) response.get("items");
        if (items.isEmpty()) throw new RuntimeException("Video private or removed");
        final Map snippet = (Map) items.get(0).get("snippet");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            return new VideoInfo((String) snippet.get("title"), dateFormat.parse((String) snippet.get("publishedAt")));
        } catch (ParseException e) {
            throw new RuntimeException("Video date parsing error", e);
        }
    }

    public static String extractVideoIdFromYoutubeUrl(String url) {
        //https://www.youtube.com/watch?v=044VwC_uptU
        Matcher matcher = Pattern.compile("^https?://www\\.youtube\\.com/watch\\?v=([^&]+)").matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            // https://youtu.be/1I1cy6z-FgY
            matcher = Pattern.compile("^https?://youtu.be/(.*)$").matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        throw new RuntimeException("Cannot resolve video id");
    }

    @SuppressWarnings("unchecked")
    public Optional<String> getCodeVideoFromYoutube(String id){
        String code = null;
        final Map response = restTemplate.getForObject(forGetCodeOfVideoUrl,Map.class, API_KEY, id);
        final List<Map> items = (List<Map>) response.get("items");

        if (items.isEmpty()) return Optional.empty();
        final Map snippet = (Map) items.get(0).get("snippet");
        List<String> tags = (List)snippet.get("tags");
        for (String tag : tags) {
            //"\\d{4}-\\d{2}-\\d{2}(_\\d{1,2})?([-_][km])?"
            Matcher matcher = Pattern.compile(codeVideoPatternRegExp).matcher(tag);
            if (matcher.find()) {
                code = matcher.group(0);
                log.info("Code for video with id = " + id + ": " + code);
            }
        }
        return Optional.ofNullable(code);
    }

    public static String extractDocIdFromUrl(String url) {
        //https://docs.google.com/document/d/1iWY8qI5Qn1V_90VpzfhFDTyAcNgati9u6sTv-A-gWQg/edit?usp=sharing
        //https://drive.google.com/file/d/0BwGttgSD-WcTbTJFbWplN1hwcFU/view?usp=sharing
       return extractIdFromUrl(url);
    }

    public DocInfo getDocInfo(String id) {
        final DocInfo doc = restTemplate.getForObject(DocOrImageInfoUrl, DocInfo.class, id, API_KEY);
        Assert.notNull(doc.title);
        return doc;
    }

    public static String extractImageIdFromUrl(String url) {
        return extractIdFromUrl(url);
    }

    public ImageInfo getImageInfo(String id) {
        final ImageInfo imageInfo = restTemplate.getForObject(DocOrImageInfoUrl, ImageInfo.class, id, API_KEY);
        Assert.notNull(imageInfo.title);
        return imageInfo;
    }

    private static String extractIdFromUrl(String url) {
        Matcher matcher = Pattern.compile("^https?://(docs|drive)\\.google\\.com/(document|file)/d/([^/]+)").matcher(url);
        if (matcher.find()) {
            return matcher.group(3);
        }
        throw new RuntimeException("Cannot resolve id");
    }

    /** Authorizes the installed application to access user's protected data. */
    private static Credential authorize() {
        GoogleClientSecrets clientSecrets;
        GoogleAuthorizationCodeFlow flow;
        try {
            // load client secrets
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                    new InputStreamReader(GoogleService.class.getResourceAsStream("/client_secrets.json")));
            if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                    || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
                throw new RuntimeException("Enter Client ID and Secret from https://code.google.com/apis/console/?api=drive into .../client_secrets.json");
            }
            // set up authorization code flow
            flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets,
                    Collections.singleton(DriveScopes.DRIVE_FILE)).setDataStoreFactory(dataStoreFactory)
                    .build();
            // authorize
            return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        } catch (IOException e) {
            throw new RuntimeException("Google authorization error", e);
        }
    }

    public File uploadToGoogleDrive(String url, String title) {
        Resource resource = resourceLoader.getResource("file:" + driveDir);
        if (!resource.exists()) {
            throw new RuntimeException("Error locating Google Drive dir "+ driveDir);
        }

        java.io.File dataStoreDir;
        try {
            dataStoreDir = resource.getFile();
        } catch (IOException e) {
            throw new RuntimeException("Error locating Google Drive dir "+ driveDir);
        }

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(dataStoreDir);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Google Drive initialization error", e);
        }
        // authorization
        Credential credential = authorize();
        // set up the global Drive instance
        drive = new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
        // run command
        return uploadFile(false, url, title);
    }

    /** Uploads a file using either resumable or direct media upload. */
    private static File uploadFile(boolean useDirectUpload, String url, String title) {

        InputStream data;
        try {
            data = new URL(url).openStream();
        } catch (IOException e) {
            log.warn("Url {} not accessible",url);
            throw new RuntimeException("Url not accessible");
        }

        File fileMetadata = new File();
        fileMetadata.setTitle(title);

        InputStreamContent mediaContent = new InputStreamContent("", new BufferedInputStream(data));

        Drive.Files.Insert insert;
        try {
            insert = drive.files().insert(fileMetadata, mediaContent);
        } catch (IOException e) {
            throw new RuntimeException("Google Drive file inserting error", e);
        }

        MediaHttpUploader uploader = insert.getMediaHttpUploader();
        uploader.setDirectUploadEnabled(useDirectUpload);
        log.info("Uploading {}...", title);
        File execute;
        try {
            execute = insert.execute();
        } catch (IOException e) {
            throw new RuntimeException("Google Drive insert execution error", e);
        }
        sharedAccess(execute.getId());
        log.info("Done");
        return execute;
    }

    private static void sharedAccess(String id){
        JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {
                throw new RuntimeException(e.getMessage());
            }

            @Override
            public void onSuccess(Permission permission, HttpHeaders responseHeaders)throws IOException {
                log.info("Permission ID: " + permission.getId());
            }
        };
        BatchRequest batch = drive.batch();
        Permission userPermission = new Permission()
                .setType("anyone")
                .setRole("reader");
        try {
            drive.permissions().insert(id, userPermission)
                    .setFields("id")
                    .queue(batch, callback);

            batch.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        Permission domainPermission = new Permission() //возможно понадобиться в дальнейшем
//                .setType("domain")
//                .setRole("reader")
//                .setDomain("ii.ayfaar.org");
//        drive.permissions().insert(fileId, domainPermission)
//                .setFields("id")
//                .queue(batch, callback);


    }

    public static class VideoInfo {
        public Date publishedAt;
        public String title;

        public VideoInfo(String title, Date publishedAt) {
            this.title = title;
            this.publishedAt = publishedAt;
        }
    }

    @NoArgsConstructor
    public static class DocInfo {
        public String title;
        public String iconLink;
        public String thumbnailLink;
        public String mimeType;
        public String downloadUrl;
        public String fileExtension;
        public Integer fileSize;
    }

    @NoArgsConstructor
    public static class ImageInfo {
        public String title;
        public String downloadUrl;
        public String mimeType;
        public String thumbnailLink;
    }
}