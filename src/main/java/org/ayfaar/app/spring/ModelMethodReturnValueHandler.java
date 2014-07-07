package org.ayfaar.app.spring;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import static org.ayfaar.app.utils.ValueObjectUtils.getModelMap;

/**
 * Decorator that detects a declared , and
 * injects support if required
 * @author y.lebid@spryflash.com
 *
 */
public class ModelMethodReturnValueHandler implements HandlerMethodReturnValueHandler {

    private final HandlerMethodReturnValueHandler delegate;

    public ModelMethodReturnValueHandler(HandlerMethodReturnValueHandler delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.getMethodAnnotation(Model.class) != null;
    }

    @Override
    public void handleReturnValue(Object returnValue,
                                  MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws Exception {

        Model modelAnnotation = returnType.getMethodAnnotation(Model.class);

        returnValue = getModelMap(returnValue, true, modelAnnotation.keepProperties());

        delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
    }

}