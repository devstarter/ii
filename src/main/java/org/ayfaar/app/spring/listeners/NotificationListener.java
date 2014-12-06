package org.ayfaar.app.spring.listeners;


import com.pushbullet.PushbulletClient;
import org.ayfaar.app.events.BasicPushEvent;
import org.ayfaar.app.events.TermUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static com.pushbullet.Builder.pushbullet;

/**
 * Created by Pas8sion on 09.11.2014.
 */
@Component
public class NotificationListener implements ApplicationListener<BasicPushEvent> {

    @Autowired
    private PushbulletClient pushbulletClient;
    @Value("${pushbullet.channel}")
    private String channel;
    @Autowired
    private ApplicationContext ctx;

    @Override
    public void onApplicationEvent(final BasicPushEvent event) {

        if (ctx.getParent()!=null) return; // fix to avoid duplications

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(event instanceof TermUrl){
                    pushbullet(pushbulletClient).pushes().channel(channel).link(event.getTitle(), event.getMessage(), ((TermUrl)event).getUrl());
                }else {
                    pushbullet(pushbulletClient).pushes().channel(channel).note(event.getTitle(), event.getMessage());
                }
            }
        }).start();
    }
}
