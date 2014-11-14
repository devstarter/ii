package org.ayfaar.app.spring.handler;

import org.ayfaar.app.spring.events.DefaultRestErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

@Component
public class DefaultRestErrorResolver implements RestErrorResolver {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRestErrorResolver.class);
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public BusinessError resolveError(ServletWebRequest request, Object handler, Exception ex) {

//        ex.printStackTrace(System.out);
        logger.error("Exception", ex);
        eventPublisher.publishEvent(new DefaultRestErrorEvent(this,ex));

        if (ex instanceof NullPointerException) {
            String stackTrace = "";
            for (StackTraceElement element : ex.getStackTrace()) {
                stackTrace += "\n" + element.toString();
            }
            return new BusinessError("UNDEFINED", ex.toString(), stackTrace);
        }

        Throwable mySQLIntegrityConstraintViolationException = findInChain("com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException", ex);
        if (mySQLIntegrityConstraintViolationException != null) {
            return new BusinessError(mySQLIntegrityConstraintViolationException.getMessage(), ex.getMessage());
        }

        return new BusinessError(ex.toString(), ex.getMessage());
    }

    private Throwable findInChain(Class<?> clazz, Throwable ex) {
        if (clazz.isInstance(ex)) {
            return ex;
        } else if (ex.getCause() != null) {
            return findInChain(clazz, ex.getCause());
        }
        return null;
    }

    private Throwable findInChain(String className, Throwable ex) {
        if (ex.getClass().getName().equals(className)) {
            return ex;
        } else if (ex.getCause() != null) {
            return findInChain(className, ex.getCause());
        }
        return null;
    }

    public String tell(){
        eventPublisher.publishEvent(new DefaultRestErrorEvent(this,new Exception("my test ex")));
        return "12234534";
    }
}
