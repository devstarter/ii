package org.ayfaar.app.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionUtils {
    public static <I, R> List<R> transform(Collection<I> c, Transformer<I, R> t) {
        List<R> result = new ArrayList<R>(c.size());
        for (I item : c) {
            result.add(t.transform(item));
        }
        return result;
    }
}
