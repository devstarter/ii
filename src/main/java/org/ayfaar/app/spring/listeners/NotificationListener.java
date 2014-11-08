package org.ayfaar.app.spring.listeners;

import org.ayfaar.app.spring.events.IINotificationEvent;
import org.ayfaar.app.spring.handler.DefaultRestErrorResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class NotificationListener implements ApplicationListener<IINotificationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);
	
	@Override
	public void onApplicationEvent(IINotificationEvent event) {
		logger.error("IIevent caught: "+event.getSource());
		
	}

}
