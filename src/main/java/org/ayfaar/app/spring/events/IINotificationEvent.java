package org.ayfaar.app.spring.events;

import org.springframework.context.ApplicationEvent;

public class IINotificationEvent extends ApplicationEvent {

	private static final long serialVersionUID = -3578207442809095068L;

	public IINotificationEvent(Object source) {
		super(source);
	}

}
