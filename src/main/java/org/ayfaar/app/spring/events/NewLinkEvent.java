package org.ayfaar.app.spring.events;

/**
 * Created by Pas8sion on 17.11.2014.
 */
public class NewLinkEvent extends BasicPushEvent {
    public NewLinkEvent(String term, String alias, Integer linkId) {
        super();
        title = "INFO: Создана связь (Создана связь (" + term + " + " + alias + ")";
        message = "link id: " + linkId
                + " удалить связь " + getRemoveLink(linkId)
                + "\nhttp://ii.ayfaar.org/#" + term
                + "\nhttp://ii.ayfaar.org/#" + alias;
    }
}
