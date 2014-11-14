package org.ayfaar.app.spring.listeners;

import org.ayfaar.app.spring.events.DefaultRestErrorEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created by Pas8sion on 09.11.2014.
 */
@Component
public class NotificationListener implements ApplicationListener {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("d " + event);
    }

    protected void sendToPushBulletChannel(String message){

    }

}
