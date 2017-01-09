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

        // read
		GoogleSpreadsheetTranslator googleSpreadsheetTranslator = context.getBean(GoogleSpreadsheetTranslator.class);
		googleSpreadsheetTranslator.setBaseRange("Topic");
		Stream<TranslationItem> values = googleSpreadsheetTranslator.read();

        // write all
        List<TranslationItem> valuesList = values.collect(Collectors.toList());
        for (TranslationItem item : valuesList) {
            item.setOrigin(item.getOrigin() + "1");
            item.setTranslation(item.getTranslation() + "2");
        }
        googleSpreadsheetTranslator.write(valuesList.stream());

        // write one
        googleSpreadsheetTranslator.write(new TranslationItem(Optional.of(3), "Some origin 3", "Some translation 3"));
        googleSpreadsheetTranslator.write(new TranslationItem(Optional.of(5), "Some origin 5", "Some translation 5"));

        System.out.println("done");
    }

    @Bean
    public DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean() {
        return new DozerBeanMapperFactoryBean();
    }
}