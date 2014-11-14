package org.ayfaar.app.spring.events;

import org.ayfaar.app.spring.handler.DefaultRestErrorResolver;

import javax.mail.internet.MimeMessage;

/**
 * Created by Pas8sion on 14.11.2014.
 */
public class EmailNotifierEvent extends BasicPushEvent{

    private MimeMessage mimeMessage;
    public EmailNotifierEvent(Runnable emailNotifier,  MimeMessage mimeMessage) {
        super(emailNotifier);
        this.mimeMessage = mimeMessage;

        System.out.println(mimeMessage);
    }

    public MimeMessage getMimeMessage() {
        return mimeMessage;
    }
}
