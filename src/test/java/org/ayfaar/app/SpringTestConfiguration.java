package org.ayfaar.app;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mock.web.MockServletContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.ServletContext;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "org.ayfaar.app")
@PropertySource({
        "classpath:database.properties",
        "classpath:debug.properties",
        "classpath:mail.properties",
        "classpath:app.properties"
})
@ImportResource({
        "classpath:hibernate.xml",
        "classpath:spring-basic.xml"
})
@EnableAspectJAutoProxy
public class SpringTestConfiguration {
    @Bean // for @PropertySource work
    public PropertySourcesPlaceholderConfigurer pspc(){
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ServletContext servletContext() {
        return new MockServletContext();
    }
}