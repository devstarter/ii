package org.ayfaar.app.spring.handler;

import org.ayfaar.app.events.QuietException;
import org.ayfaar.app.utils.exceptions.ConfirmationRequiredException;
import org.ayfaar.app.utils.exceptions.LogicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

@Component
public class DefaultRestErrorResolver implements RestErrorResolver {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRestErrorResolver.class);
//    @Autowired
//    private ApplicationEventPublisher eventPublisher;

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public BusinessError resolveError(ServletWebRequest request, Object handler, Exception ex) {

        if (ex instanceof LogicalException){
            String message = ex instanceof ConfirmationRequiredException
                    ? ((ConfirmationRequiredException) ex).action.getId().toString()
                    : ex.toString();
            return new BusinessError(((LogicalException) ex).getCode().name(), message, null);
        }

//        ex.printStackTrace(System.out);
        if (!(ex instanceof QuietException)) {
            logger.error("Exception", ex);
        }

        if (ex instanceof NullPointerException) {
            String stackTrace = "";
            for (StackTraceElement element : ex.getStackTrace()) {
                stackTrace += "\n" + element.toString();
            }
//            eventPublisher.publishEvent(new DefaultRestErrorEvent("Exception, UNDEFINED:",ex.toString() + "\n" + stackTrace));
            return new BusinessError("UNDEFINED", ex.toString(), stackTrace);
        }

        Throwable mySQLIntegrityConstraintViolationException = findInChain("com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException", ex);
        if (mySQLIntegrityConstraintViolationException != null) {
//            eventPublisher.publishEvent(new DefaultRestErrorEvent("Exception:",mySQLIntegrityConstraintViolationException.getMessage() + "\n" + ex.getMessage()));
            return new BusinessError(mySQLIntegrityConstraintViolationException.getMessage(), ex.getMessage());
        }
        if (!(ex instanceof QuietException)) {
//            eventPublisher.publishEvent(new DefaultRestErrorEvent("Exception", ex.toString()));
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

}