package org.ayfaar.app.event;

/**
 * Created by Pas8sion on 14.11.2014.
 */
public class NewQuoteLinkEvent extends LinkPushEvent {
    public NewQuoteLinkEvent(String termName, String itemNumber, String quote, Integer linkId) {
        super("Связь " + termName + " + " + itemNumber, termName);
        message = quote + "\nlink id: " + linkId + " " + getRemoveLink(linkId);
    }
}
