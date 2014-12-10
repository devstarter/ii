package org.ayfaar.app;


import org.ayfaar.app.events.PushEvent;
import org.ayfaar.app.spring.listeners.NotificationListener;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.mockito.Mockito.*;


public class NotificationListenerTest {

    @Test
    public void senderToPushBulletTest() {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringTestConfiguration.class);
        NotificationListener listener = ctx.getBean(NotificationListener.class);

        PushEvent mock = mock(PushEvent.class);
        when(mock.getTitle()).thenReturn("title test");
        when(mock.getMessage()).thenReturn("message test");

        listener.onApplicationEvent(mock);
    }

}
