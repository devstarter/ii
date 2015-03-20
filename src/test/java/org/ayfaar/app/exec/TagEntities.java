package org.ayfaar.app.exec;

import org.ayfaar.app.SpringTestConfiguration;
import org.ayfaar.app.utils.TermsTaggingUpdater;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TagEntities {

    public static void main(String[] args) {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringTestConfiguration.class);
        TermsTaggingUpdater entityUpdater = ctx.getBean(TermsTaggingUpdater.class);

//        entityUpdater.updateAllQuotes();
//        entityUpdater.updateAllContent();
        entityUpdater.updateAllTerms();
    }
}
