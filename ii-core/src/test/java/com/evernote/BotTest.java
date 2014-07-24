package com.evernote;

import org.ayfaar.ii.synchronization.evernote.EvernoteBot;

public class BotTest {
//    @Test
    public void test() throws Exception {
        EvernoteBot bot = new EvernoteBot();
        bot.init();
        bot.getExportNotes();
    }
}
