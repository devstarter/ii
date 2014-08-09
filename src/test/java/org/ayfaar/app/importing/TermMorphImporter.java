package org.ayfaar.app.importing;

import org.ayfaar.app.SpringTestConfiguration;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.TermMorph;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TermMorphImporter {

    private static ApplicationContext ctx;

    public static void main(String[] args) {
        ctx = new AnnotationConfigApplicationContext(SpringTestConfiguration.class);

        LinkDao linkDao = ctx.getBean(LinkDao.class);
        CommonDao commonDao = ctx.getBean(CommonDao.class);

        for (Link link : linkDao.getAll()) {
            if (link.getType() != null && link.getWeight() != null
                    && link.getType() == 1 && link.getWeight() == -1) {
                commonDao.save(new TermMorph(((Term)link.getUid2()).getName(), link.getUid1().getUri()));
                commonDao.remove(link.getUid2());
            }
        }
    }
}
