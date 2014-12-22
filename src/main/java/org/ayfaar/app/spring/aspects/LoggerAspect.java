package org.ayfaar.app.spring.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.ayfaar.app.utils.MethodSignatureLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LoggerAspect {
    @Autowired
    private MethodSignatureLogger logger;

    @Before("execution(* org.ayfaar.app.controllers.*.*(..))")
    public void methodsCalls(JoinPoint jp) {
        logger.log(jp.getSignature().toString(), jp.getArgs());
    }
}
