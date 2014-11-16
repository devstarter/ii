package org.ayfaar.app.utils;

import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.spring.events.EmailNotifierEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailNotifier {
    private static final String FROM = "ii@ayfaar.org";
    private static final String TO = "sllouyssgort@gmail.com";

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired JavaMailSender mailSender;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

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
        send(helper.getMimeMessage());
    }

    public void newLink(String term, String alias, Integer linkId) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setFrom(FROM);
            helper.setTo(TO);
            helper.setSubject("Создана связь (" + term + " + " + alias + ")");
            helper.setText("link id: " + linkId
                    + " удалить связь " + getRemoveLink(linkId)
                    + "\nhttp://ii.ayfaar.org/#" + term
                    + "\nhttp://ii.ayfaar.org/#" + alias);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        send(helper.getMimeMessage());
    }

    private String getRemoveLink(Integer linkId) {
        return "http://ii.ayfaar.org/api/link/remove/" + linkId;
    }

    public void rate(Term term, Item item, String quote, Integer linkId) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setFrom(FROM);
            helper.setTo(TO);
            helper.setSubject("Связь через поиск (" + term.getName() + " + " + item.getNumber() + ")");
            helper.setText(quote +
                    (linkId == null ? "\n\nНе создана по причине возможной дубликации"
                            : "удалить связь "+getRemoveLink(linkId))
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        send(helper.getMimeMessage());
    }

    private void send(final MimeMessage message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mailSender.send(message);
            }
        });
        //eventPublisher.publishEvent(new EmailNotifierEvent(this, message));
    }
}
