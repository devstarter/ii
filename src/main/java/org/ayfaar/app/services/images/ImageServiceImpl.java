package org.ayfaar.app.services.images;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Image;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.services.topics.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ImageServiceImpl implements ImageService {
    private final CommonDao commonDao;
    private final TopicService topicService;
    private List<Image> allImages;

    @Autowired
    public ImageServiceImpl(CommonDao commonDao, TopicService topicService) {
        this.commonDao = commonDao;
        this.topicService = topicService;
    }

    @PostConstruct
    private void init() {
        log.info("Images loading...");

        allImages = commonDao.getAll(Image.class);

        log.info("Images loaded");
    }

    @Override
    public void reload() {
        init();
    }

    @Override
    public List<Image> getAllImages(){
        return allImages;
    }

    @Override
    public Map<String, String> getAllUriNames(){
        return  allImages.stream().collect(Collectors.toMap(UID::getUri, Image::getName));
    }

    @Override
    public void registerImage(Image image) {
        allImages.add(image);
    }

    @Override
    public void removeImage(Image image) {
        allImages.removeIf(i -> Objects.equals(i.getId(), image.getId()));
    }

    @Override
    public Map<String, String> getImagesKeywords(){
        return StreamEx.of(allImages).toMap(UID::getUri, image -> topicService.getAllLinkedWith(image.getUri())
                .map(tp -> tp.topic().getName())
                .joining(", "));
    }

    @Override
    public Image getByUri(String uri){
        return allImages.stream().filter(image -> image.getUri() == uri).findFirst().get();
    }
}
