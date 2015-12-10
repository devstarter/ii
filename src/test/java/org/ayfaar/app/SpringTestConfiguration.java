package org.ayfaar.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.ServletContext;

@Configuration
//@EnableTransactionManagement
//@ComponentScan(basePackages = "org.ayfaar.app")
//@PropertySource({
//        "classpath:database.properties",
//        "classpath:debug.properties",
//        "classpath:mail.properties",
//        "classpath:app.properties"
//})
//@ImportResource({
//        "classpath:hibernate.xml",
//        "classpath:spring-basic.xml"
//})
//@EnableAspectJAutoProxy
public class SpringTestConfiguration {
    @Bean
    public ServletContext servletContext() {
        return new MockServletContext();
    }
}