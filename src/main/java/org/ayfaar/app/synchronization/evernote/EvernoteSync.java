package org.ayfaar.app.synchronization.evernote;

import org.ayfaar.app.controllers.TermController;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.spring.Model;
import org.ayfaar.app.utils.EmailNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

import static org.ayfaar.app.utils.UriGenerator.generate;

@Controller
@RequestMapping("evernote")
@EnableScheduling
public class EvernoteSync {
    @Autowired EvernoteBot bot;
    @Autowired LinkDao linkDao;
    @Autowired CommonDao commonDao;
    @Autowired TermController termController;
    @Autowired EmailNotifier emailNotifier;

    @RequestMapping("sync")
    @Scheduled(fixedDelay = 300000, initialDelay = 60000) // after each 5 min
    @Model
    public void sync() throws Exception {
        bot.init();
        List<Link> synced = new ArrayList<Link>();
        List<EvernoteBot.QuoteLink> potentialLinks = bot.getPotentialLinks();

        for (EvernoteBot.QuoteLink potentialLink : potentialLinks) {
            String itemUri = generate(Item.class, potentialLink.getItem());
            String termUri = generate(Term.class, potentialLink.getTerm());

            List<Link> existingLists = linkDao.getByUris(itemUri, termUri);

            if (existingLists.size() > 0 && !potentialLink.getAllowed()) {
                bot.setTag(potentialLink.getGuid(), EvernoteBot.LINK_EXIST_TAG_NAME);
                continue;
            }

            Term term = commonDao.get(Term.class, termUri);
            if (term == null) {
                if (!potentialLink.getAllowed()) {
                    bot.setTag(potentialLink.getGuid(), EvernoteBot.TERM_NOT_EXIST_TAG_NAME);
                    continue;
                }
                term = termController.add(potentialLink.getTerm());
            }
            Item item = commonDao.get(Item.class, itemUri);
            if (item == null) {
                bot.setTag(potentialLink.getGuid(), EvernoteBot.ITEM_NOT_EXIST_TAG_NAME);
                continue;
            }
            if (!item.getContent().contains(potentialLink.getQuote())) {
                bot.setTag(potentialLink.getGuid(), EvernoteBot.QUOTE_ALTERED_TAG_NAME);
                continue;
            }

            Link link = linkDao.save(new Link(term, item, potentialLink.getQuote()));
            synced.add(link);
            emailNotifier.newQuoteLink(potentialLink.getTerm(), potentialLink.getItem(),
                    potentialLink.getQuote(), link.getLinkId());
            bot.removeNote(potentialLink.getGuid());
        }
    }
}
