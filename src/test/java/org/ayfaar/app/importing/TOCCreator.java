package org.ayfaar.app.importing;

import org.ayfaar.app.synchronization.mediawiki.TOCSync;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TOCCreator {

    private static ApplicationContext ctx;

    public static void main(String[] args) throws Exception {
        ctx = new AnnotationConfigApplicationContext(SpringConfiguration.class);

        TOCSync tocSync = ctx.getBean(TOCSync.class);

        tocSync.synchronize();
    }
}
