package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.dao.TermMorphDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.TermMorph;
import org.ayfaar.app.utils.AliasesMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("link")
public class LinkController {
    @Autowired LinkDao linkDao;
    @Autowired TermDao termDao;
    @Autowired ItemDao itemDao;
    @Autowired TermController termController;
    @Autowired JavaMailSender mailSender;
    @Autowired AliasesMap aliasesMap;
    @Autowired TermMorphDao termMorphDao;

    @RequestMapping(value = "addQuote", method = POST)
    @ResponseBody
    public Integer link(@RequestParam("term") String termName,
                        @RequestParam("item") String itemNumber,
                        @RequestParam String quote) throws MessagingException {
        if (termName.isEmpty() || itemNumber.isEmpty()) {
            return null;
        }
        Term term = termDao.getByName(termName);
        if (term == null) {
            if (aliasesMap.get(termName) != null) {
                term = aliasesMap.get(termName).getTerm();
            } else {
                term = termController.add(termName);
            }
        }
        Item item = itemDao.getByNumber(itemNumber);
        Link link = linkDao.save(new Link(term, item, quote.isEmpty() ? null : quote));

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom("ii@ayfaar.org");
        helper.setTo("ylebid@gmail.com");
        helper.setSubject("Создана связь (" + term.getName() + " + " + itemNumber + ")");
        helper.setText(quote + "\nlink id: " + link.getLinkId() + "\nhttp://ii.ayfaar.org/#"
                + term.getName().replace(" ", "+"));
        mailSender.send(helper.getMimeMessage());

        return link.getLinkId();
    }

    @RequestMapping(value = "addAlias", method = POST)
    @ResponseBody
    public Integer addAlias(@RequestParam("term1") String term,
                            @RequestParam("term2") String alias,
                            @RequestParam Byte type) throws MessagingException {
        Term primTerm = termDao.getByName(term);
        if (primTerm == null) {
            primTerm = termController.add(term);
        }
        if (type != null && type == 0) {
            // Morph
            termMorphDao.save(new TermMorph(alias, primTerm.getUri()));
            return 1;
        }
        Term aliasTerm = termDao.getByName(alias);
        if (aliasTerm == null) {
            aliasTerm = termController.add(alias);
        }
        Link link = linkDao.save(new Link(primTerm, aliasTerm, type));

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom("ii@ayfaar.org");
        helper.setTo("ylebid@gmail.com");
        helper.setSubject("Создана связь (" + term + " + " + alias + ")");
        helper.setText("link id: " + link.getLinkId()
                        + " <a href=\"http://ii.ayfaar.org/api/link/remove/" + link.getLinkId() + "\">удалить</a>"
                + "\nhttp://ii.ayfaar.org/#" + term
                + "\nhttp://ii.ayfaar.org/#" + alias);
        mailSender.send(helper.getMimeMessage());

        return link.getLinkId();
    }

    @RequestMapping("remove/{id}")
    public void remove(@PathVariable Integer id) {
        linkDao.remove(id);
    }
}
