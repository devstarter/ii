package org.ayfaar.app.spring.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.ayfaar.app.utils.LogTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MethodSignatureLogger {
    @Autowired
    private LogTarget logTarget;

    //@Before("execution(* org.ayfaar.app..*(..))")
    //@Before("execution(* org.ayfaar.app.controllers.NewSearchController.*(..))")
    //@AfterThrowing("execution(* org.ayfaar.app.controllers.ItemController.*(..))")
    @Before("execution(* org.ayfaar.app.controllers.ItemController.*(..))")
    public void methodsCalls(JoinPoint jp) {
        String signature = "METHOD'S SIGNATURE IS: " + jp.getSignature().toString();
        logTarget.log(signature, jp.getArgs());
    }
}
