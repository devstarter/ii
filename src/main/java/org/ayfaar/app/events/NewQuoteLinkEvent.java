package org.ayfaar.app.events;

/**
 * Created by Pas8sion on 14.11.2014.
 */
public class NewQuoteLinkEvent extends BasicPushEvent{

    public NewQuoteLinkEvent(String termName, String itemNumber, String quote, Integer linkId) {
        super();
        title = "Создана связь (" + termName + " + " + itemNumber + ")";
        message = quote + "\nlink id: " + linkId + " " + getRemoveLink(linkId) + "\n" + getUrlToTerm(termName);
    }

}
