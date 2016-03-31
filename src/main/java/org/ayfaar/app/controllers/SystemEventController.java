package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.SystemEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("api/event")
public class SystemEventController {
    @Inject CommonDao commonDao;

    @RequestMapping
    public List<SystemEvent> getList(@PageableDefault Pageable pageable) {
        return commonDao.getPage(SystemEvent.class, pageable);
    }

    @RequestMapping("clear")
    public void clear() {
        commonDao.getAll(SystemEvent.class)
                .parallelStream()
                .forEach(commonDao::remove);
    }
}
