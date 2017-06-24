package org.ayfaar.app.exec;

import org.ayfaar.app.SpringTestDevConfiguration;
import org.ayfaar.app.dao.*;
import org.ayfaar.app.model.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class CleanDBFromUnusedUID {
    private static UIDDao uidDao;

    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringTestDevConfiguration.class);
        CommonDao commonDao = ctx.getBean(CommonDao.class);
        uidDao = ctx.getBean(UIDDao.class);

        List<UID> implementedUIDs = new ArrayList<UID>();
        implementedUIDs.addAll(commonDao.getAll(Article.class));
        implementedUIDs.addAll(commonDao.getAll(Category.class));
        implementedUIDs.addAll(commonDao.getAll(Item.class));
        implementedUIDs.addAll(commonDao.getAll(Term.class));

        List<String> implementedUris = new ArrayList<String>();
        for (UID uid : implementedUIDs) {
            implementedUris.add(uid.getUri());
        }
        System.out.println("Implemented UIDs count:" + implementedUris.size());

        List<String> allUri = uidDao.getAll();
        System.out.println("All UIDs count:" + allUri.size());

        List<String> notUsedUris = getNotUsedUris(allUri, implementedUris);
        System.out.println("Not implemented UIDs count:" + notUsedUris.size());
        remove(notUsedUris);
    }

    private static List<String> getNotUsedUris(List<String> allUri, List<String> implementedUris) {
        List<String> notUsedUris = new ArrayList<String>();
        for(String uri : allUri) {
            if(!implementedUris.contains(uri)) {
                notUsedUris.add(uri);
            }
        }
        return notUsedUris;
    }

    private static void remove(List<String> uris) {
        for(String uri : uris) {
            uidDao.removeByUri(uri);
            System.out.println(uri + " removed");
        }
    }
}
