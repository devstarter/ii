package org.ayfaar.app;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.services.SpreadSheetService;
import org.dozer.spring.DozerBeanMapperFactoryBean;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


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

        BufferedReader br;
        while (true) {
            try {
                br = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Enter range: ");
                String input = br.readLine();

                SpreadSheetService.test(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Bean
    public DozerBeanMapperFactoryBean dozerBeanMapperFactoryBean() {
        return new DozerBeanMapperFactoryBean();
    }
}