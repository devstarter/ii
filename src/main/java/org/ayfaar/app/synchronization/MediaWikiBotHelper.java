package org.ayfaar.app.synchronization;

import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

@Component
public class MediaWikiBotHelper {
    private MediaWikiBot bot;
    private PrintStream out;

    public MediaWikiBotHelper() throws UnsupportedEncodingException {
        out = new PrintStream(System.out, true, "UTF-8");
    }

    public MediaWikiBot getBot() {
        if (bot == null) {
            bot = new MediaWikiBot("http://direct.ayfaar.org/mediawiki/");
            bot.login("admin", "ayfaar");
        }
        return bot;
    }

    public void saveArticle(SimpleArticle article) {
        Assert.hasLength(article.getTitle());
        Assert.hasLength(article.getText());

        getBot().writeContent(article);
        out.println(article.getTitle());
    }
}
