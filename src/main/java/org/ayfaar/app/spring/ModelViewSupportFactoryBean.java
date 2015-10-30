package org.ayfaar.app.spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Modified Spring internal Return value handlers, and wires up a decorator
 * to add support for @Model
 *
 * @author y.lebid@spryflash.com
 *
 */
public class ModelViewSupportFactoryBean implements InitializingBean {

    @Autowired RequestMappingHandlerAdapter adapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        HandlerMethodReturnValueHandlerComposite returnValueHandlers = adapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> handlers = new ArrayList<HandlerMethodReturnValueHandler>(returnValueHandlers.getHandlers());

        ModelMethodReturnValueHandler modelHandler = null;

        for (HandlerMethodReturnValueHandler handler : handlers) {
            if (handler instanceof RequestResponseBodyMethodProcessor)
            {
                modelHandler = new ModelMethodReturnValueHandler(handler);
                break;
            }
        }

        handlers.add(0, modelHandler);
//        handlers.add(0, new CsvMessageConverter());
        adapter.setReturnValueHandlers(handlers);
    }


}
