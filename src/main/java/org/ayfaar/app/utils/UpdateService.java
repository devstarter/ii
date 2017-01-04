package org.ayfaar.app.utils;


import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.event.TermAddEvent;
import org.ayfaar.app.services.itemRange.ItemRangeService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.inject.Inject;


@Service
@Slf4j
public class UpdateService {
    @Inject TermService termService;
    @Inject TermsFinder termsFinder;
    @Inject ItemRangeService itemRangeService;

    @EventListener
    @Async
    public void updateTermServices(TermAddEvent termAddEvent){
        termService.reload();
        termsFinder.updateTermParagraphForTerm(termAddEvent.getTerm());
        itemRangeService.reload();
    }
}
