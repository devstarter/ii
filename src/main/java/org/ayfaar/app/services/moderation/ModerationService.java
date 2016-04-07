package org.ayfaar.app.services.moderation;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.annotations.Moderated;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.SystemEvent;
import org.ayfaar.app.utils.exceptions.ConfirmationRequiredException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

import static java.lang.String.format;


@Service
@Slf4j
public class ModerationService {
    private final StandardEvaluationContext context;
    private final SpelExpressionParser parser;
    private final CommonDao commonDao;

    private final ThreadLocal<MethodEntry> threadLocal = new ThreadLocal<>();

    @Inject
    protected ModerationService(CommonDao commonDao, BeanFactory beanFactory) {
        this.commonDao = commonDao;
        context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));
        parser = new SpelExpressionParser();
    }

    /*public void notice(String action, Object... args) {
        String message = MessageFormatter.arrayFormat(action, args).getMessage();
        log.info(message);
        final SystemEvent event = new SystemEvent();
        event.setMessage(message);
        commonDao.save(event);
    }*/

    public void check(Action action) {
        if (!getCurrentAccessLevel().accept(action.getRequiredAccessLevel())) {
            registerConfirmationRequirement(action);
            throw new ConfirmationRequiredException(action);
        }
    }

    private void registerConfirmationRequirement(Action action) {
        final MethodEntry entry = threadLocal.get();
        if (entry == null) throw new RuntimeException("No moderated method for action "+action);

        final SystemEvent event = new SystemEvent();
        event.setMessage(format("Action %s for user %s required confirmation", action, getCurrentUserEmail()));
        event.setUser(getCurrentUserEmail());
        event.setCommand(buildCommand(entry));
        event.setAction(action);
        commonDao.save(event);
    }

    private String buildCommand(MethodEntry entry) {
        String command = entry.command + "(";
        for (int i = 0; entry.args.length > i ; i++) {
            Object arg = entry.args[i];
            if (arg == null) {
                command += "null,";
            } else if (arg instanceof String) {
                command += format("'%s',", ((String) arg).replace("'", "\\'"));
            } else if (arg instanceof Number) {
                command += format("%s,", arg);
            } else {
                throw new RuntimeException("Cannot serialize "+arg);
            }
        }
        return command.substring(0, command.lastIndexOf(",")) + ")";
    }

    public void confirm(SystemEvent event) {
        if (!getCurrentAccessLevel().accept(event.getAction().getRequiredAccessLevel()))
            throw new ConfirmationRequiredException(event.getAction());
        // perform command
        parser.parseExpression(event.getCommand()).getValue(context);
        log.info("{} confirmed by user {}", event.getMessage(), getCurrentUserEmail());
        event.setConfirmedByUser(getCurrentUserEmail());
        commonDao.save(event);
    }

    private AccessLevel getCurrentAccessLevel() {
        return AccessLevel.ROLE_ADMIN;
    }

    private String getCurrentUserEmail() {
        return "sllouyssgort@gmail.com";
    }

    void checkMethod(Moderated moderated, Object[] args) {
        MethodEntry entry = threadLocal.get();
        if (entry == null) {
            entry = new MethodEntry(moderated.value(), moderated.command(), args);
            threadLocal.set(entry);
        }
        check(moderated.value());
    }
}
