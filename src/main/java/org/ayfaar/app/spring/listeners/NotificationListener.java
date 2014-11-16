package org.ayfaar.app.spring.listeners;


import com.pushbullet.PushbulletClient;
import org.ayfaar.app.spring.events.BasicPushEvent;
import org.ayfaar.app.spring.events.DefaultRestErrorEvent;
import org.ayfaar.app.spring.events.EmailNotifierEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static com.pushbullet.Builder.pushbullet;
import static com.pushbullet.Builder.registerClient;

/**
 * Created by Pas8sion on 09.11.2014.
 */
@Component
public class NotificationListener implements ApplicationListener<BasicPushEvent> {

    @Autowired
    private PushbulletClient pushbulletClient;
    @Value("${pushbullet.channel}")
    private String channel;


    @Override
    public void onApplicationEvent(BasicPushEvent event) {

        String title = "";
        String message = "";

        if (event instanceof DefaultRestErrorEvent) {
            title = "DefaultRestErrorEvent";
            message = ((DefaultRestErrorEvent) event).getEx().getMessage();

        } else if (event instanceof EmailNotifierEvent) {
            title = "EmailNotifierEvent";
            MimeMessage letter = ((EmailNotifierEvent) event).getMimeMessage();
            try {
                message = "From " + letter.getFrom() + " To" + letter.getReplyTo() + "\n" + letter.getSubject();
            } catch (MessagingException e) {
                message = e.getMessage();
            }
        }

        sendToPushBulletChannel(title, message);
    }

    protected void sendToPushBulletChannel(String title, String message){

        registerClient(pushbulletClient);
        pushbullet().pushes().channel(channel).note(title, message);

    }

}
