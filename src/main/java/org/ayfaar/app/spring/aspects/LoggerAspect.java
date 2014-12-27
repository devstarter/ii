package org.ayfaar.app.spring.aspects;

import lombok.Getter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Aspect
//@Component
//@Scope(value = "request", proxyMode= ScopedProxyMode.TARGET_CLASS)
@Scope("customScope")
public class LoggerAspect {

    private List<MethodProvider> methods = new ArrayList<MethodProvider>();

    @Before("execution(* org.ayfaar.app..*.*(..))")
    //@Before("execution(* org.ayfaar.app.utils.*.*(..))")
    public void methodsCalls(JoinPoint jp) {
        log(jp.getSignature().toString(), jp.getArgs());
    }
    private void log(String signature, Object[] params) {
        methods.add(new MethodProvider(signature, params));
    }

    public List<MethodProvider> getLog() {
        return methods;
    }

    public void clear() {
        methods.clear();
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
