package org.ayfaar.app.importing;

import org.apache.commons.io.FileUtils;
import org.ayfaar.app.Application;
import org.ayfaar.app.controllers.TermController;
import org.ayfaar.app.dao.ArticleDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Article;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.TermService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.compile;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class,
        initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("remote")
@WebAppConfiguration
public class VocabularyImporter {
    private Article currentArticle;
    @Inject private ArticleDao articleDao;
    @Inject private TermDao termDao;
    @Inject private LinkDao linkDao;
    @Inject private TermController termController;
    @Inject private TermService termService;
//    private static String skipUntilNumber = "1.0780";
//    private static boolean saveAllowed = true;

    @Test
    public void main() throws IOException {
        currentArticle = null;

        for(String line: FileUtils.readLines(new File("D:\\projects\\вёрстка томов ИИ\\9 том\\термины.txt"))) {

            Matcher matcher = compile("\\|([^\\|]+)\\|\\s[–\\-—]\\s(.+)").matcher(line);
            if (matcher.find()) {
                if (currentArticle != null) {
                    saveItem();
                    currentArticle = null;
                }
                String term = matcher.group(1);
                String body = matcher.group(2);
                if(body.indexOf("см. ") != 0) {
                    currentArticle = new Article(term, body);
                } /*else {
                    String alias = body.replace("см. ", "").replace("«", "").replace("»", "").replace(".", "");
                    saveAliases(alias, term);
                }*/
            } else if (currentArticle != null) {
                currentArticle.setContent(currentArticle.getContent() + "<br/>"+line);
            }
        }
        if (currentArticle != null) {
            saveItem();
        }
    }

    /*private static void saveAliases(String termName, String aliasName) {
        Term term = getTerm(termName);
        Term alias = getTerm(aliasName);
        Link link = new Link(term, alias, LinkType.ALIAS);
        linkDao.save(link);
    }*/

    private Term getTerm(String termName) {
        Term term = termDao.getByName(termName);
        if (term == null) {
            termController.add(termName, "");
            term = termDao.getByName(termName);
        }
        return term;
    }

    private void saveItem() {

        Article storedArticle = articleDao.get("name", currentArticle.getName());

        if (storedArticle != null) {
            storedArticle.setContent(currentArticle.getContent());
            storedArticle.setTaggedContent(null);
            currentArticle = storedArticle;
        }

        currentArticle.setContent(currentArticle.getContent().trim());
        System.out.print(currentArticle.getName() + ": ");
        System.out.println(currentArticle.getContent());

//        Item storedItem = commonDao.getByNumber(currentArticle.getNumber());
//        if (storedItem != null) {
//            currentArticle.setUri(currentArticle.generateUri());
//        }
        articleDao.save(currentArticle);

        termService.get(currentArticle.getName())
                .map(TermService.TermProvider::getMainOrThis)
                .map(TermService.TermProvider::getTerm)
                .ifPresent((term -> {
                    Link link = new Link(term, currentArticle);
                    linkDao.save(link);
                }));
    }
}

