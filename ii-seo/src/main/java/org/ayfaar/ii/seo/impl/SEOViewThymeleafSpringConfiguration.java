package org.ayfaar.ii.seo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
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

    @Bean // for @PropertySource work
    public PropertySourcesPlaceholderConfigurer pspc(){
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ITemplateResolver templateResolver(@Value("${templateResolver.PREFIX}")String prefix,
                                              @Value("${templateResolver.cacheTTLMs}")Long cacheTTLMs){
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        // XHTML is the default mode, but we set it anyway for better understanding of code
        templateResolver.setTemplateMode("XHTML");
        // This will convert "home" to "/WEB-INF/templates/home.html"
        templateResolver.setPrefix(prefix);
        templateResolver.setSuffix(".xhtml");
        // Template cache TTL=1h. If not set, entries would be cached until expelled by LRU
        templateResolver.setCacheTTLMs(cacheTTLMs);
        return templateResolver;
    }

    @Bean
    @Autowired
    public TemplateEngine templateEngine(ITemplateResolver templateResolver){
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

}
