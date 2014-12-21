package org.ayfaar.app.utils;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Stack;

@Component
@Scope("request")
public class LogTarget {
    private Stack<MethodProvider> methods = new Stack<MethodProvider>();

    public void log(String methodSignature, Object[] params) {
        methods.push(new MethodProvider(methodSignature, params));
    }

    public List<MethodProvider> getLog() {
        return methods;
    }

    class MethodProvider {
        String methodSignature;
        Object[] params;

        private MethodProvider(String methodSignature, Object[] params) {
            this.methodSignature = methodSignature;
            this.params = params;
        }
    }
}
