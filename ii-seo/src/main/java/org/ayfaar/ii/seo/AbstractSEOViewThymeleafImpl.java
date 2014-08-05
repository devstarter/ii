package org.ayfaar.ii.seo;

import org.apache.commons.lang.NotImplementedException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Drorzz on 05.08.2014.
 */
public abstract class AbstractSEOViewThymeleafImpl implements SEOView {

    private final static TemplateEngine templateEngine = initTemplateEngine();

    private String name;

    private Map<String, String> viewParameters;

    private class ContextMock extends AbstractContext{
        public ContextMock(Map<String, ?> viewParameters){
            super();
            this.getVariables().putAll(viewParameters);
        }

        @Override
        protected IContextExecutionInfo buildContextExecutionInfo(String templateName) {
            return new WebContextExecutionInfo(templateName, Calendar.getInstance());
        }
    }

    protected AbstractSEOViewThymeleafImpl(String name){
        this.name = name;
    }

    @Override
    public void setViewParameters(Map<String, String> viewParameters) {
        this.viewParameters = new HashMap<>(viewParameters);
    }

    @Override
    public String getHTML() {
        IContext context = new ContextMock(getParameters(viewParameters));
        return templateEngine.process(name, context);
    }

    public String getName(){
        return name;
    }

    private static TemplateEngine initTemplateEngine(){
        throw new NotImplementedException();
    }

    protected abstract Map<String,?> getParameters(Map<String, String> viewParameters);
}
