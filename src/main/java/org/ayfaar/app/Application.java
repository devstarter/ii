package org.ayfaar.app;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.translation.GoogleSpreadsheetTranslator;
import org.ayfaar.app.translation.TranslationItem;
import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@EnableJpaRepositories
@EntityScan("org.ayfaar.app.model")
@ComponentScan("org.ayfaar.app")
@ImportResource({"classpath:hibernate.xml", "classpath:spring-basic.xml"})
@EnableCaching
@EnableAspectJAutoProxy
@Slf4j
public class Application {
    public static void main(String[] args) {
        final ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        log.info("Open in browser: " + context.getEnvironment().getProperty("this-url"));
    }

    @Bean
    public DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean() {
        return new DozerBeanMapperFactoryBean();
    }
}