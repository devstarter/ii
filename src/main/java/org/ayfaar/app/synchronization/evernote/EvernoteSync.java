package org.ayfaar.app.synchronization.evernote;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import org.ayfaar.app.controllers.TermController;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.spring.Model;
import org.ayfaar.app.utils.EmailNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static org.ayfaar.app.utils.UriGenerator.generate;

@Controller
@RequestMapping("evernote")
//@EnableScheduling
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
        List<EvernoteBot.ExportNote> exportNotes = bot.getExportNotes();

        for (EvernoteBot.ExportNote exportNote : exportNotes) {
            if (exportNote.getGuid() == null || exportNote.getGuid().isEmpty()) {
                throw new RuntimeException("No GUID");
            }
            if (exportNote instanceof EvernoteBot.QuoteLink) {
                syncQuoteLink((EvernoteBot.QuoteLink) exportNote);
            }
            if (exportNote instanceof EvernoteBot.RelatedTerms) {
                syncRelatedTerms((EvernoteBot.RelatedTerms) exportNote);
            }
        }
    }

    private void syncRelatedTerms(EvernoteBot.RelatedTerms relatedTerms) throws Exception {
        if (relatedTerms.getTerms().size() < 2) {
            bot.setTag(relatedTerms.getGuid(), EvernoteBot.LACK_OF_TERMS_TAG_NAME);
            return;
        }
        String mainTermName = relatedTerms.getTerms().get(0);
        relatedTerms.getTerms().remove(0);
        String mainTermUri = generate(Term.class, mainTermName);

        Term mainTerm = commonDao.get(Term.class, mainTermUri);
        if (mainTerm == null) {
            if (!relatedTerms.getAllowed()) {
                bot.setTag(relatedTerms.getGuid(), EvernoteBot.TERM_NOT_EXIST_TAG_NAME);
                return;
            }
            mainTerm = termController.add(mainTermName);
        }

        for (String termName : relatedTerms.getTerms()) {
            String termUri = generate(Term.class, termName);
            Term term = commonDao.get(Term.class, termUri);
            if (term == null) {
                if (!relatedTerms.getAllowed()) {
                    bot.setTag(relatedTerms.getGuid(), EvernoteBot.TERM_NOT_EXIST_TAG_NAME);
                    return;
                }
                term = termController.add(termName);
            }
            Link link = linkDao.save(new Link(mainTerm, term, relatedTerms.getType()));

            emailNotifier.newLink(mainTermName, termName, link.getLinkId());
        }
        bot.removeNote(relatedTerms.getGuid());
    }

    private void syncQuoteLink(EvernoteBot.QuoteLink potentialLink) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {
        String itemUri = generate(Item.class, potentialLink.getItem());

        for (String termName : potentialLink.getTerms()) {
            String termUri = generate(Term.class, termName);

            List<Link> existingLists = linkDao.getByUris(itemUri, termUri);

            if (existingLists.size() > 0 && !potentialLink.getAllowed()) {
                bot.setTag(potentialLink.getGuid(), EvernoteBot.LINK_EXIST_TAG_NAME);
                return;
            }

            Term term = commonDao.get(Term.class, termUri);
            if (term == null) {
                if (!potentialLink.getAllowed()) {
                    bot.setTag(potentialLink.getGuid(), EvernoteBot.TERM_NOT_EXIST_TAG_NAME);
                    return;
                }
                term = termController.add(termName);
            }
            Item item = commonDao.get(Item.class, itemUri);
            if (item == null) {
                bot.setTag(potentialLink.getGuid(), EvernoteBot.ITEM_NOT_EXIST_TAG_NAME);
                return;
            }
            if (potentialLink.getQuote() != null && !item.getContent().contains(potentialLink.getQuote())) {
                bot.setTag(potentialLink.getGuid(), EvernoteBot.QUOTE_ALTERED_TAG_NAME);
                return;
            }

            Link link = linkDao.save(new Link(term, item, potentialLink.getQuote()));

            emailNotifier.newQuoteLink(termName, potentialLink.getItem(),
                    potentialLink.getQuote(), link.getLinkId());
        }
        bot.removeNote(potentialLink.getGuid());
    }
}
