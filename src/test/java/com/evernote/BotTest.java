package com.evernote;

import org.ayfaar.app.synchronization.evernote.EvernoteBot;
import org.junit.Test;

public class BotTest {
    @Test
    public void test() throws Exception {
        EvernoteBot bot = new EvernoteBot();
        bot.init();
        bot.getPotentialLinks();
    }
}
