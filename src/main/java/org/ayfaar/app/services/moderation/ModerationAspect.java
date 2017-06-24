package org.ayfaar.app.services.moderation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.ayfaar.app.annotations.Moderated;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Aspect
@Component
public class ModerationAspect {
    private final ModerationService moderationService;

    @Inject
    public ModerationAspect(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @Pointcut("@annotation(org.ayfaar.app.annotations.Moderated)")
    public void moderatedAnnotated() {}

    @Around(value = "moderatedAnnotated() && @annotation(moderated)")
    public Object around(ProceedingJoinPoint pjp, Moderated moderated) throws Throwable {
        moderationService.checkMethod(moderated, pjp.getArgs());
        return pjp.proceed();
    }
}
