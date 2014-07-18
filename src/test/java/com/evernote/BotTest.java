package com.evernote;

import org.ayfaar.app.synchronization.evernote.EvernoteBot;

public class BotTest {
//    @Test
    public void test() throws Exception {
        EvernoteBot bot = new EvernoteBot();
        bot.init();
        bot.getExportNotes();
    }
}
