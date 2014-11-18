package org.ayfaar.app.spring.listeners;


import com.pushbullet.PushbulletClient;
import org.ayfaar.app.spring.events.BasicPushEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

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

        sendToPushBulletChannel(event.getTitle(), event.getMessage());
    }

    protected void sendToPushBulletChannel(final String title,final String message){

        new Thread(new Runnable() {
            @Override
            public void run() {
                registerClient(pushbulletClient);
                pushbullet().pushes().channel(channel).note(title, message);
            }
        }).start();

    }

}
