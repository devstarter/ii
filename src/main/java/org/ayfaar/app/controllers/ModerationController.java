package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.SystemEvent;
import org.ayfaar.app.services.moderation.ModerationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("api/moderation")
public class ModerationController {
    @Inject CommonDao commonDao;
    @Inject ModerationService service;

    @RequestMapping
    public List<SystemEvent> get(@PageableDefault Pageable pageable) {
        // todo filter by access level of current user
        // and filter by empty confirmedByUser
        // show only my users (this user can be linked with another as children for personal moderation)
        return commonDao.getPage(SystemEvent.class, pageable);
    }

    @RequestMapping("{id}/confirm")
    public void confirm(@PathVariable Integer id) {
        final SystemEvent event = commonDao.get(SystemEvent.class, id);
        service.confirm(event);
    }
}
