package org.ayfaar.app.services.itemRange;

import one.util.streamex.StreamEx;
import org.ayfaar.app.model.ItemsRange;

import java.util.List;

public interface ItemRangeService {

    void reload();

    List<ItemsRange> getWithCategories();

    StreamEx<String> getParagraphsByTerm(String term);
}
