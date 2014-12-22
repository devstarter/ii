package org.ayfaar.app.utils;


import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Stack;


public class MethodSignatureLogger {
    private Stack<MethodProvider> methods = new Stack<MethodProvider>();

    public void log(String methodSignature, Object[] params) {
        methods.push(new MethodProvider(methodSignature, params));
    }

    public List<MethodProvider> getLog() {
        return methods;
    }

    public class MethodProvider {
        @Getter
        String methodSignature;
        @Getter
        Object[] params;

        private MethodProvider(String methodSignature, Object[] params) {
            this.methodSignature = methodSignature;
            this.params = params;
        }
    }
}
