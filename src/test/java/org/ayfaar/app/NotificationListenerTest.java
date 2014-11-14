package org.ayfaar.app;


import org.ayfaar.app.spring.handler.DefaultRestErrorResolver;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class NotificationListenerTest {


    @Test
    public void listenerTest() {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringTestConfiguration.class);
        DefaultRestErrorResolver p = ctx.getBean(DefaultRestErrorResolver.class);
       // p.tell();
    }
}
