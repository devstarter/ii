package org.ayfaar.app.utils;

import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailNotifier {
    private static final String FROM = "ii@ayfaar.org";
    private static final String TO = "sllouyssgort@gmail.com";
    @Autowired JavaMailSender mailSender;

    public void newQuoteLink(String termName, String itemNumber, String quote, Integer linkId) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setFrom(FROM);
            helper.setTo(TO);
            helper.setSubject("Создана связь (" + termName + " + " + itemNumber + ")");
            helper.setText(quote + "\nlink id: " + linkId + " " + getRemoveLink(linkId) + "\nhttp://ii.ayfaar.org/#"
                    + termName.replace(" ", "+"));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        mailSender.send(helper.getMimeMessage());
    }

    public void newLink(String term, String alias, Integer linkId) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setFrom(FROM);
            helper.setTo(TO);
            helper.setSubject("Создана связь (" + term + " + " + alias + ")");
            helper.setText("link id: " + linkId
                    + " " + getRemoveLink(linkId)
                    + "\nhttp://ii.ayfaar.org/#" + term
                    + "\nhttp://ii.ayfaar.org/#" + alias);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        mailSender.send(helper.getMimeMessage());
    }

    private String getRemoveLink(Integer linkId) {
        return "<a href=\"http://ii.ayfaar.org/api/link/remove/" + linkId + "\">удалить ссылку</a>";
    }

    public void rate(Term term, Item item, String quote, boolean possibleDuplication) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setFrom(FROM);
            helper.setTo(TO);
            helper.setSubject("Связь через поиск (" + term.getName() + " + " + item.getNumber() + ")");
            helper.setText(quote + (possibleDuplication ? "\n\nНе создана по причине возможной дубликации" : ""));
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        mailSender.send(helper.getMimeMessage());
    }
}
