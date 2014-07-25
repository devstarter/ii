package org.ayfaar.ii.seo;

import org.ayfaar.ii.model.Article;
import org.junit.Test;

public class InitTest {

    @Test
    /**
     * проверяем что мы имеем доступ к класам из основного проекта
     */
    public void dependencyTest() {
        new Article();
    }
}
