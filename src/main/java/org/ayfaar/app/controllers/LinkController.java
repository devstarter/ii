package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
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

    @RequestMapping(value = "add", method = POST)
    @ResponseBody
    public Integer link(@RequestParam("term") String termName,
                        @RequestParam("item") String itemNumber,
                        @RequestParam String quote) throws MessagingException {
        if (termName.isEmpty() || itemNumber.isEmpty()) {
            return null;
        }
        Term term = termDao.getByName(termName);
        if (term == null) {
            term = termController.add(termName);
        }
        Item item = itemDao.getByNumber(itemNumber);
        Link link = linkDao.save(new Link(term, item, quote));

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom("ii@ayfaar.org");
        helper.setTo("ylebid@gmail.com");
        helper.setSubject("Создана связь (" + termName + " + " + itemNumber + ")");
        helper.setText(quote + "\nlink id: " + link.getLinkId() + "\nhttp://ii.ayfaar.org/#" + termName);
        mailSender.send(helper.getMimeMessage());

        return link.getLinkId();
    }
}
