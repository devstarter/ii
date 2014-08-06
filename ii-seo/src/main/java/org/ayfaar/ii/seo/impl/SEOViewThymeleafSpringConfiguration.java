package org.ayfaar.ii.seo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
 * Created by Drorzz on 06.08.2014.
 */
@Configuration
@PropertySource("classpath:seoThymeleafSpring.properties")
@Lazy
public class SEOViewThymeleafSpringConfiguration {
    @Autowired
    Environment env;
    @Autowired
    ITemplateResolver templateResolver;

    @Bean
    public ITemplateResolver templateResolver(){
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        // XHTML is the default mode, but we set it anyway for better understanding of code
        templateResolver.setTemplateMode("XHTML");
        // This will convert "home" to "/WEB-INF/templates/home.html"
        templateResolver.setPrefix(env.getProperty("templateResolver.PREFIX"));
        templateResolver.setSuffix(".xhtml");
        // Template cache TTL=1h. If not set, entries would be cached until expelled by LRU
        templateResolver.setCacheTTLMs(Long.getLong(env.getProperty("templateResolver.cacheTTLMs")));
        return templateResolver;
    }

    @Bean
    public TemplateEngine templateEngine(){
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

}
