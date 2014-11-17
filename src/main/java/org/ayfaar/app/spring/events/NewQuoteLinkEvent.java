package org.ayfaar.app.spring.events;

import org.ayfaar.app.utils.EmailNotifier;
import javax.mail.internet.MimeMessage;

/**
 * Created by Pas8sion on 14.11.2014.
 */
public class NewQuoteLinkEvent extends BasicPushEvent{


    public NewQuoteLinkEvent(String termName, String itemNumber, String quote, Integer linkId) {
        super();
        title = "INFO: Создана связь (" + termName + " + " + itemNumber + ")";
        message = quote + "\nlink id: " + linkId + " " + getRemoveLink(linkId) + "\nhttp://ii.ayfaar.org/#"
                + termName.replace(" ", "+");
    }

}
