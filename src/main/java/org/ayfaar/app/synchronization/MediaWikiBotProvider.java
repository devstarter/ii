package org.ayfaar.app.synchronization;

import net.sourceforge.jwbf.mediawiki.bots.MediaWikiBot;
import org.springframework.stereotype.Component;

@Component
public class MediaWikiBotProvider {
    private MediaWikiBot bot;

    public MediaWikiBot getBot() {
        if (bot == null) {
            bot = new MediaWikiBot("http://mediawiki/");
            bot.login("admin", "ayfaar");
        }
        return bot;
    }
}
