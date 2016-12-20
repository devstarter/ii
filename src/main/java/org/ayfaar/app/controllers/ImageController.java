package org.ayfaar.app.controllers;


import com.google.api.services.drive.model.File;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Image;
import org.ayfaar.app.services.images.ImageService;
import org.ayfaar.app.services.links.LinkService;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.utils.GoogleService;
import org.ayfaar.app.utils.SearchSuggestions;
import org.dozer.Mapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.*;

import static java.lang.Math.min;
import static org.ayfaar.app.utils.UriGenerator.generate;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.util.Assert.hasLength;

@Slf4j
@RestController
@RequestMapping("api/image")
public class ImageController {
    @Inject CommonDao commonDao;
    @Inject GoogleService googleService;
    @Inject ImageService imageService;
    @Inject LinkService linkService;
    @Inject SearchSuggestions searchSuggestions;
    @Inject TopicService topicService;
    @Inject Mapper mapper;

    private static final int MAX_SUGGESTIONS = 7;

    @RequestMapping(method = RequestMethod.POST)
    public Image create(@RequestParam String url,
                        @RequestParam(required = false) Optional<String> name){

        Assert.hasLength(url);
        if(!url.contains("google.com")){
            File file = googleService.uploadToGoogleDrive(url, name.orElse("Новая иллюстрация"));
            url = file.getAlternateLink();
        }
        final String imgId = GoogleService.extractImageIdFromUrl(url);
        return commonDao.getOpt(Image.class,generate(Image.class,imgId))
                .orElseGet(() -> {
                    final GoogleService.ImageInfo imageInfo = googleService.getImageInfo(imgId);
                    final Image image = Image.builder()
                            .id(imgId)
                            .name(name.orElse(imageInfo.title))
                            .downloadUrl(imageInfo.downloadUrl)
                            .mimeType(imageInfo.mimeType)
                            .thumbnail(imageInfo.thumbnailLink)
                            .build();
                     commonDao.save(image);
                     imageService.registerImage(image);
                     return image;
                });
    }

    @RequestMapping()
    public List<Image> getAll() {
        return imageService.getAllImages();
    }

    @RequestMapping("{id}")
    public Image get(@PathVariable String id) {
        return commonDao.get(Image.class, generate(Image.class, id));
    }

    @RequestMapping("last")
    public List<Image> getLast(@PageableDefault(size = 9, sort = "createdAt", direction = DESC) Pageable pageable) {
        return commonDao.getPage(Image.class, pageable);
    }

    @RequestMapping(value = "update-name", method = RequestMethod.POST)
    public void updateTitle(@RequestParam String uri, @RequestParam String title) {
        hasLength(uri);
        Image image = commonDao.getOpt(Image.class, "uri", uri).orElseThrow(() -> new RuntimeException("Couldn't rename, image is not defined!"));
        image.setName(title);
        commonDao.save(image);
    }

    @RequestMapping("{id}/remove")
    public void remove(@PathVariable String id) {
        commonDao.getOpt(Image.class, "id", id).ifPresent(image -> {
            imageService.removeImage(image);
            commonDao.remove(image);
        });
    }

    @RequestMapping(value = "update-comment", method = RequestMethod.POST)
    public void updateComment(@RequestParam String uri, @RequestParam String comment) {
        hasLength(uri);
        Image image = commonDao.getOpt(Image.class, "uri", uri).orElseThrow(() -> new RuntimeException("Couldn't update comment, image is not defined!"));
        image.setComment(comment);
        commonDao.save(image);
    }

    @RequestMapping("search")
    public List<ImageEx> search(@RequestParam String q){
        Map<String, String> allSuggestions = new LinkedHashMap<>();
        List<Suggestions> items = new ArrayList<>();
        items.add(Suggestions.IMAGES);
        items.add(Suggestions.TOPIC);   //image-keywords
        for (Suggestions item : items) {
            Queue<String> queriesQueue = searchSuggestions.getQueue(q);
            List<Map.Entry<String, String>> suggestions = getSuggestions(queriesQueue, item);
            allSuggestions.putAll(searchSuggestions.getAllSuggestions(q,suggestions));
        }

        return StreamEx.of(allSuggestions.entrySet())
                .map(entry -> {
                    final ImageEx imageEx = mapper.map(imageService.getByUri(entry.getKey()), ImageEx.class);
                    if (!entry.getValue().equals(imageEx.getName().toLowerCase())) imageEx.hint = entry.getValue();
                    return imageEx;
                })
                .toList();
    }

    private List<Map.Entry<String, String>> getSuggestions(Queue<String> queriesQueue, Suggestions item) {
        List<Map.Entry<String, String>> suggestions = new ArrayList<>();

        while (suggestions.size() < MAX_SUGGESTIONS && queriesQueue.peek() != null) {
            List<? extends Map.Entry<String, String>> founded = null;
            Map<String, String> mapUriWithNames = null;

            switch (item) {
                case TOPIC://image-keywords
                    mapUriWithNames = imageService.getImagesKeywords();
                    break;
                case IMAGES:
                    mapUriWithNames = imageService.getAllUriNames();
                    break;
            }

            founded = searchSuggestions.getSuggested(queriesQueue.poll(), suggestions, mapUriWithNames.entrySet(), Map.Entry::getValue);

            suggestions.addAll(founded.subList(0, min(MAX_SUGGESTIONS - suggestions.size(), founded.size())));
        }

        Collections.sort(suggestions, (e1, e2) -> Integer.valueOf(e1.getValue().length()).compareTo(e2.getValue().length()));
        return suggestions;
    }

    public static class ImageEx extends Image {
        public String hint;

        public ImageEx() {
        }
    }
}
