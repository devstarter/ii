package org.ayfaar.ii.importing;

import org.ayfaar.ii.SpringTestConfiguration;
import org.ayfaar.ii.synchronization.mediawiki.TOCSync;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TOCCreator {

    private static ApplicationContext ctx;

    public static void main(String[] args) throws Exception {
        ctx = new AnnotationConfigApplicationContext(SpringTestConfiguration.class);

        TOCSync tocSync = ctx.getBean(TOCSync.class);

        tocSync.synchronize();
    }
}
