package org.ayfaar.app.importing;

import org.apache.commons.io.FileUtils;
import org.ayfaar.app.SpringTestConfiguration;
import org.ayfaar.app.controllers.TermController;
import org.ayfaar.app.dao.ArticleDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Article;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.compile;

public class VocabularyImporter {
    private static Article currentArticle;
    private static ApplicationContext ctx;
    private static ArticleDao articleDao;
    private static TermDao termDao;
    private static LinkDao linkDao;
    private static TermController termController;
//    private static String skipUntilNumber = "1.0780";
//    private static boolean saveAllowed = true;

    public static void main(String[] args) throws Docx4JException, IOException {
        currentArticle = null;

        ctx = new AnnotationConfigApplicationContext(SpringTestConfiguration.class);
        articleDao = ctx.getBean(ArticleDao.class);
        termDao = ctx.getBean(TermDao.class);
        linkDao = ctx.getBean(LinkDao.class);
        termController = ctx.getBean(TermController.class);

        for(String line: FileUtils.readLines(new File("D:\\PROJECTS\\ayfaar\\ii-app\\src\\main\\text\\Том 9 - Словарь размечено.txt"))) {

            Matcher matcher = compile("9\\.\\|([^\\|]+)\\|\\s[–\\-—]\\s(.+)").matcher(line);
            if (matcher.find()) {
                if (currentArticle != null) {
                    saveItem();
                    currentArticle = null;
                }
                String term = matcher.group(1);
                String body = matcher.group(2);
                if(body.indexOf("см. ") == 0) {
                    String alias = body.replace("см. ", "").replace("«", "").replace("»", "").replace(".", "");
                    saveAliases(alias, term);
                } else {
                    currentArticle = new Article(term, body);
                }
            } else if (currentArticle != null) {
                currentArticle.setContent(currentArticle.getContent() + "<br/>"+line);
            }
        }
    }

    private static void saveAliases(String termName, String aliasName) {
        Term term = getTerm(termName);
        Term alias = getTerm(aliasName);
        Link link = new Link(term, alias, Link.ALIAS);
        linkDao.save(link);
    }

    private static Term getTerm(String termName) {
        Term term = termDao.getByName(termName);
        if (term == null) {
            termController.add(termName, "");
            term = termDao.getByName(termName);
        }
        return term;
    }

    private static void saveItem() {

        currentArticle.setContent(currentArticle.getContent().trim());
        System.out.print(currentArticle.getName() + ": ");
        System.out.println(currentArticle.getContent());

//        Item storedItem = commonDao.getByNumber(currentArticle.getNumber());
//        if (storedItem != null) {
//            currentArticle.setUri(currentArticle.generateUri());
//        }
        articleDao.save(currentArticle);

        Term term = getTerm(currentArticle.getName());
        Link link = new Link(term, currentArticle);
        linkDao.save(link);
    }
}

