package org.ayfaar.app.services.moderation;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.annotations.Moderated;
import org.ayfaar.app.event.EventPublisher;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.event.SimplePushEvent;
import org.ayfaar.app.model.ActionEvent;
import org.ayfaar.app.model.PendingAction;
import org.ayfaar.app.utils.CurrentUserProvider;
import org.ayfaar.app.utils.exceptions.ConfirmationRequiredException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;

import static java.lang.String.format;
import static org.slf4j.helpers.MessageFormatter.arrayFormat;


@Service
@Slf4j
public class ModerationService {
    private final StandardEvaluationContext context;
    private final SpelExpressionParser parser;
    private final CommonDao commonDao;
    private final CurrentUserProvider currentUserProvider;
    private final EventPublisher publisher;

    private final ThreadLocal<MethodEntry> threadLocal = new ThreadLocal<>();

    @Inject
    protected ModerationService(CommonDao commonDao, BeanFactory beanFactory, CurrentUserProvider currentUserProvider, EventPublisher publisher) {
        this.commonDao = commonDao;
        this.currentUserProvider = currentUserProvider;
        this.publisher = publisher;
        context = new StandardEvaluationContext();
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));
        parser = new SpelExpressionParser();
    }

    public void notice(Action action, Object... args) {
        String message = arrayFormat(action.message, args).getMessage() + " пользователем " + getCurrentUserName();
        log.info(message);
        final ActionEvent actionEvent = new ActionEvent();
        actionEvent.setMessage(message);
        // указать такущего пользователя
        actionEvent.setCreatedBy(getCurrentUserId());
        // указать action
        actionEvent.setAction(action);
        commonDao.save(actionEvent);
    }

    public void check(Action action, Object... args) {
        if (!currentUserProvider.getCurrentAccessLevel().accept(action.getRequiredAccessLevel())) {
            final PendingAction pendingAction = registerConfirmationRequirement(action, args);
            throw new ConfirmationRequiredException(pendingAction);
        }
    }

    private PendingAction registerConfirmationRequirement(Action action, Object[] args) {
        final MethodEntry entry = threadLocal.get();
        if (entry == null) throw new RuntimeException("No moderated method for action " + action);
        Action rootAction = entry.action;
        String actionText = "";
        if (!rootAction.name().equals(action.name())) {
            actionText += action.message != null ? arrayFormat(action.message, args).getMessage() : action.name();
            actionText += " для ";
        }
        final PendingAction pendingAction = new PendingAction();
        actionText += rootAction.message != null ? arrayFormat(rootAction.message, entry.args).getMessage() : rootAction.name();
        // fixme: в случае отсутствия пользователя в getCurrentUserName() ошибка
        pendingAction.setMessage(format("%s пользователем %s", actionText, getCurrentUserName()));
        pendingAction.setInitiatedBy(getCurrentUserId());
        pendingAction.setCommand(buildCommand(entry));
        pendingAction.setAction(rootAction);
        publisher.publishEvent(new SimplePushEvent("Confirmation request", pendingAction.getMessage()));
        return commonDao.save(pendingAction);
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

    public void cancel(Integer id) {
        final PendingAction pendingAction = commonDao.getOpt(PendingAction.class, id)
                .orElseThrow(() -> new RuntimeException("Action not found"));
        boolean ownAction = getCurrentUserId().equals(pendingAction.getInitiatedBy());
        if (!ownAction && !currentUserProvider.getCurrentAccessLevel().accept(pendingAction.getAction().getRequiredAccessLevel()))
            throw new ConfirmationRequiredException(pendingAction);
        commonDao.remove(pendingAction);
    }

    public void confirm(PendingAction pendingAction) {
        if (!currentUserProvider.getCurrentAccessLevel().accept(pendingAction.getAction().getRequiredAccessLevel()))
            throw new ConfirmationRequiredException(pendingAction);
        // perform command
        parser.parseExpression(pendingAction.getCommand()).getValue(context);
        log.info("{} confirmed by user {}", pendingAction.getMessage(), getCurrentUserId());
        pendingAction.setConfirmedBy(getCurrentUserId());
        pendingAction.setConfirmedAt(new Date());
        commonDao.save(pendingAction);
    }

    private Integer getCurrentUserId() {
        return currentUserProvider.get().isPresent() ? currentUserProvider.get().get().getId() : null;
    }

    private String getCurrentUserName() {
        // fixme: не продуман кейс отсутсвия пользователя
        return currentUserProvider.get().isPresent() ? currentUserProvider.get().get().getName() : "Аноним";
    }

    void checkMethod(Moderated moderated, Object[] args) {
//        MethodEntry entry = threadLocal.get();
//        if (entry == null) {
        MethodEntry entry = new MethodEntry(moderated.value(), moderated.command(), args);
        threadLocal.set(entry);
//        }
        check(moderated.value(), args);
    }
}
