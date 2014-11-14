package org.ayfaar.app.spring.events;

import org.ayfaar.app.utils.EmailNotifier;
import javax.mail.internet.MimeMessage;

/**
 * Created by Pas8sion on 14.11.2014.
 */
public class EmailNotifierEvent extends BasicPushEvent{

    private MimeMessage mimeMessage;
    public EmailNotifierEvent(EmailNotifier emailNotifier,  MimeMessage mimeMessage) {
        super(emailNotifier);
        this.mimeMessage = mimeMessage;
    }

    public MimeMessage getMimeMessage() {
        return mimeMessage;
    }
}
