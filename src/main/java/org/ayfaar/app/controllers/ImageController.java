package org.ayfaar.app.controllers;


import com.google.api.services.drive.model.File;
import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Image;
import org.ayfaar.app.services.images.ImageService;
import org.ayfaar.app.utils.GoogleService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;


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
                    return commonDao.save(image);
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
            commonDao.remove(image);
        });
    }
}
