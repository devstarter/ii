package org.ayfaar.ii.seo.impl;

import lombok.Getter;
import lombok.Setter;
import org.ayfaar.ii.seo.SEOView;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.*;
import org.thymeleaf.exceptions.NotInitializedException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Drorzz on 05.08.2014.
 */
public abstract class AbstractSEOViewThymeleafImpl implements SEOView {
    private @Setter TemplateEngine templateEngine;

    private @Getter String name;
    private Map<String, String> viewParameters;

    public AbstractSEOViewThymeleafImpl(String name,TemplateEngine templateEngine){
        this.name = name;
        this.templateEngine = templateEngine;
    }

    @Override
    public void setViewParameters(Map<String, String> viewParameters) {
        this.viewParameters = new HashMap<String, String>(viewParameters);
    }

    @Override
    public String getHTML() {
        if(isInitializedTemplateEngine()){
            throw new NotInitializedException("Template engine not initialized"){};
        }
        Context context = new Context();
        context.setVariables(getParameters(viewParameters));
        return templateEngine.process(name, context);
    }

    private boolean isInitializedTemplateEngine(){
        return templateEngine == null;
    }

    // Тут наследники должны реализовать логику получения данных для подставления в шаблон
    protected abstract Map<String,?> getParameters(Map<String, String> viewParameters);
}
