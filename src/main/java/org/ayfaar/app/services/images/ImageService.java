package org.ayfaar.app.services.images;

import org.ayfaar.app.model.Image;

import java.util.List;
import java.util.Map;

public interface ImageService {

    void reload();

    List<Image> getAllImages();

    Map<String, String> getAllUriNames();

    void registerImage(Image image);

    void removeImage(Image image);

    Map<String, String> getImagesKeywords();

    Image getByUri(String uri);
}
