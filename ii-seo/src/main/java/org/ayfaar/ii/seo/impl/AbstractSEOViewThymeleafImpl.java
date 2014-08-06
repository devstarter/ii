package org.ayfaar.ii.seo.impl;

import lombok.Getter;
import lombok.Setter;
import org.ayfaar.ii.seo.SEOView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.*;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Drorzz on 05.08.2014.
 */
public abstract class AbstractSEOViewThymeleafImpl implements SEOView {
    private static @Setter String PREFIX = "/WEB-INF/templates/";
    private final static String SUFFIX = ".xhtml";
    private final static String TEMPLATE_MODE = "XHTML";
    private final static long CACHE_TTL_MS = 3600000L;
    private static TemplateEngine templateEngine;


    private @Getter String name;
    private Map<String, String> viewParameters;

    public AbstractSEOViewThymeleafImpl(String name){
        this.name = name;
    }

    @Override
    public void setViewParameters(Map<String, String> viewParameters) {
        this.viewParameters = new HashMap<>(viewParameters);
    }

    @Override
    public String getHTML() {
        Context context = new Context();
        context.setVariables(getParameters(viewParameters));
        if(templateEngine == null){
            initTemplateEngine();
        }
        return templateEngine.process(name, context);
    }

    private static ITemplateResolver getTemplateResolver(){
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        // XHTML is the default mode, but we set it anyway for better understanding of code
        templateResolver.setTemplateMode(TEMPLATE_MODE);
        // This will convert "home" to "/WEB-INF/templates/home.html"
        templateResolver.setPrefix(PREFIX);
        templateResolver.setSuffix(SUFFIX);
        // Template cache TTL=1h. If not set, entries would be cached until expelled by LRU
        templateResolver.setCacheTTLMs(CACHE_TTL_MS);
        return templateResolver;
    }

    private static void initTemplateEngine(){
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(getTemplateResolver());
    }

    // Тут наследники должны реализовать логику получения данных для подставления в шаблон
    protected abstract Map<String,?> getParameters(Map<String, String> viewParameters);
}
