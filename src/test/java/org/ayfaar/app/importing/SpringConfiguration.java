package org.ayfaar.app.importing;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.mock.web.MockServletContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.ServletContext;

@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "org.ayfaar.app"/*, excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, value = Synchronizer.class)*/)
/*@PropertySource({
        "file:D:\\PROJECTS\\ayfaar\\ii-app\\src\\main\\webapp\\WEB-INF\\database.properties",
        "file:D:\\PROJECTS\\ayfaar\\ii-app\\src\\main\\webapp\\WEB-INF\\debug.properties"
})*/
/*@ImportResource({
        "file:D:\\projects\\ayfaar\\ii-app\\src\\main\\resources\\hibernate.xml",
        "file:D:\\projects\\ayfaar\\ii-app\\src\\main\\resources\\spring-basic.xml"
})*/
/*@PropertySource({
        "file:d:\\WEB\\MyProjects\\II\\src\\main\\webapp\\WEB-INF\\database.properties",
        "file:D:\\WEB\\MyProjects\\II\\src\\main\\webapp\\WEB-INF\\debug.properties"
})
@ImportResource({
        "file:D:\\WEB\\MyProjects\\II\\src\\main\\resources\\hibernate.xml",
        "file:D:\\WEB\\MyProjects\\II\\src\\main\\resources\\spring-basic.xml"
})*/

@PropertySource({
        "classpath:database.properties",
        "classpath:debug.properties"
})
@ImportResource({
        "classpath:hibernate.xml",
        "classpath:spring-basic.xml"
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