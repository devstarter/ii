package org.ayfaar.app.services.images;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ImageServiceImpl implements ImageService {
    private final CommonDao commonDao;
    private List<Image> allImages;

    @Autowired
    public ImageServiceImpl(CommonDao commonDao) {
        this.commonDao = commonDao;
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
        return  allImages.stream().collect(Collectors.toMap(image -> image.getUri(), image -> image.getName()));
    }
}
