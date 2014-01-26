package org.ayfaar.app.utils;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mock.web.MockServletContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.ServletContext;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "org.ayfaar.app")
@PropertySource({
        "file:D:\\PROJECTS\\ayfaar\\ii-app\\src\\main\\webapp\\WEB-INF\\database.properties",
        "file:D:\\PROJECTS\\ayfaar\\ii-app\\src\\main\\webapp\\WEB-INF\\debug.properties"
})
@ImportResource({
        "file:D:\\projects\\ayfaar\\ii-app\\src\\main\\resources\\hibernate.xml",
        "file:D:\\projects\\ayfaar\\ii-app\\src\\main\\resources\\spring-basic.xml"
})
@EnableAspectJAutoProxy
public class SpringConfiguration {
    @Bean // for @PropertySource work
    public PropertySourcesPlaceholderConfigurer pspc(){
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ServletContext servletContext() {
        return new MockServletContext();
    }
}