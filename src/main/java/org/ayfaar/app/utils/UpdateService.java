package org.ayfaar.app.utils;


import antlr.debug.NewLineEvent;
import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.event.LinkRemovedEvent;
import org.ayfaar.app.event.LinksRemovedEvent;
import org.ayfaar.app.event.TermAddedeEvent;
import org.ayfaar.app.services.itemRange.ItemRangeService;
import org.ayfaar.app.services.links.LinkService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.inject.Inject;


@Service
@Slf4j
public class UpdateService {
    @Inject TermService termService;
    @Inject LinkService linkService;
    @Inject TermsFinder termsFinder;
    @Inject ItemRangeService itemRangeService;

    @EventListener
    @Async
    public void updateTermServices(TermAddedeEvent termAddedeEvent){
        termService.reload();
        termsFinder.updateTermParagraphForTerm(termAddedeEvent.getTerm());
        itemRangeService.reload();
    }

    @EventListener
    @Async
    public void on(NewLineEvent event){
        updateAll();
    }

    @EventListener
    @Async
    public void on(LinkRemovedEvent event){
        updateAll();
    }

    @EventListener
    @Async
    public void on(LinksRemovedEvent event){
        updateAll();
    }

    public void updateAll(){
        termService.reload();
        linkService.reload();
    }
}
