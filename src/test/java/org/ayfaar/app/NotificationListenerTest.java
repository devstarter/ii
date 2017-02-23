package org.ayfaar.app;


import org.ayfaar.app.event.PushEvent;
import org.ayfaar.app.spring.listeners.NotificationListener;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.mockito.Mockito.*;

@Ignore
public class NotificationListenerTest {

    @Test
    public void senderToPushBulletTest() {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringTestDevConfiguration.class);
        NotificationListener listener = ctx.getBean(NotificationListener.class);

        PushEvent mock = mock(PushEvent.class);
        when(mock.getTitle()).thenReturn("title test");
        when(mock.getMessage()).thenReturn("message test");

        listener.onApplicationEvent(mock);
    }

}
