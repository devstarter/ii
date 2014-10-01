package org.ayfaar.app.exec;

import org.ayfaar.app.SpringTestConfiguration;
import org.ayfaar.app.dao.*;
import org.ayfaar.app.model.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class CleanDBFromUID {
    private static UIDDao uidDao;
    private static ArticleDao articleDao;
    private static CategoryDao categoryDao;
    private static ItemDao itemDao;
    private static TermDao termDao;

    public static void main(String[] args) {
        UriExtractor uriExtractor = new UriExtractor();

        init();

        List<Article> articles = articleDao.getAll();
        List<Category> categories = categoryDao.getAll();
        List<Item> items = itemDao.getAll();
        List<Term> terms = termDao.getAll();

        List<UIDTest> listUid = uidDao.getAll();
        String[] UIDUris = extractUIDUri(listUid);

        List<String> uris = uriExtractor.getUris(articles);
        uris.addAll(uriExtractor.getUris(categories));
        uris.addAll(uriExtractor.getUris(items));
        uris.addAll(uriExtractor.getUris(terms));

        List<String> notUsedUris = getNotUsedUris(UIDUris, uris);
        remove(notUsedUris);
    }

    private static void init() {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringTestConfiguration.class);
        uidDao = ctx.getBean(UIDDao.class);
        articleDao = ctx.getBean(ArticleDao.class);
        categoryDao = ctx.getBean(CategoryDao.class);
        itemDao = ctx.getBean(ItemDao.class);
        termDao = ctx.getBean(TermDao.class);
    }

    private static String[] extractUIDUri(List<UIDTest> uris) {
        String[] UIDUris = uris.toString().split(", ");
        UIDUris[0] = UIDUris[0].replaceFirst("\\[", "");
        UIDUris[UIDUris.length-1] = UIDUris[UIDUris.length-1].replaceFirst("\\]", "");
        return UIDUris;
    }

    private static List<String> getNotUsedUris(String[] UIDUris, List<String> uris) {
        List<String> notUsedUris = new ArrayList<String>();
        for(String uri : UIDUris) {
            if(!uris.contains(uri)) {
                notUsedUris.add(uri);
            }
        }
        return notUsedUris;
    }

    private static void remove(List<String> uris) {
        for(String uri : uris) {
            uidDao.removeByUri(uri);
        }
    }

    private static class UriExtractor<E> {
       private List<String> getUris(List<E> list) {
           List<String> set = new ArrayList<String>();

            for(int i = 0; i < list.size(); i++) {
                if(list.get(i) instanceof Article) {
                    set.add(((Article) list.get(i)).getUri());
                }
                else if(list.get(i) instanceof Category) {
                    set.add(((Category) list.get(i)).getUri());
                }
                else if(list.get(i) instanceof Item) {
                    set.add(((Item) list.get(i)).getUri());
                }
                else if(list.get(i) instanceof Term) {
                    set.add(((Term) list.get(i)).getUri());
                }
            }
            return set;
        }
    }
}
