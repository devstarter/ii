package org.ayfaar.app.services.itemRange;

import org.ayfaar.app.model.ItemsRange;
import java.util.List;
import java.util.stream.Stream;

public interface ItemRangeService {

    void reload();

    List<ItemsRange> getWithCategories();

    Stream<String> getParagraphsByTerm(String term);
}
