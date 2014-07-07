package org.ayfaar.app.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

//@Aspect
//@Repository
public class ModeratedAspect {

    private static final Logger logger = getLogger(ModeratedAspect.class);

//    @Pointcut("execution(public * com.sf.framework.ewi.controllers.UserController(..))")
//    public void userControllerMethods()  {}

    @Pointcut("execution(@org.ayfaar.app.annotations.Moderated * *(..))")
    public void moderatedMethod() {}

    @Before("moderatedMethod()")
    public void logBeforeModeratedMethod(JoinPoint joinPoint) {
        Class clazz = joinPoint.getSignature().getDeclaringType();
        String methodName = joinPoint.getSignature().getName();
    }
}
