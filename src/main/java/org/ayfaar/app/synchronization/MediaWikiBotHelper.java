package org.ayfaar.app.synchronization;

import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.SyncStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

@Component
public class MediaWikiBotHelper {
    @Autowired CommonDao commonDao;

    private MediaWikiBot bot;
    private PrintStream out;

    public MediaWikiBotHelper() throws UnsupportedEncodingException {
        out = new PrintStream(System.out, true, "UTF-8");
    }

    public MediaWikiBot getBot() {
        if (bot == null) {
            bot = new MediaWikiBot("http://mediawiki/");
//            bot = new MediaWikiBot("http://direct.ayfaar.org/mediawiki/");
            bot.login("admin", "ayfaar");
        }
        return bot;
    }

    public void push() {
        List<SyncStatus> toBeSync = commonDao.getList(SyncStatus.class, "synchronised", false);
        toBeSync.addAll(commonDao.getList(SyncStatus.class, "synchronised", null));
        for (SyncStatus status : toBeSync) {
            SimpleArticle article = new SimpleArticle(status.getArticleName());
            article.setText(status.getArticleContent());
            getBot().writeContent(article);
            status.setSynchronised(true);
            status.setSyncDate(new Date());
            commonDao.save(status);
            out.println("[Pushed] " + article.getTitle());
        }
    }

    public void saveArticle(String title, String text) {
        Assert.hasLength(title);
        Assert.hasLength(text);
        SyncStatus status = commonDao.get(SyncStatus.class, "articleName", title);
        if (status == null) {
            status = new SyncStatus();
            status.setArticleName(title);
        }
        status.setSynchronised(false);
        status.setArticleContent(text);
        commonDao.save(status);
        out.println("[Scheduled] " + title);
    }

    public void isSyncNeeded(String articleName) {

    }
}
