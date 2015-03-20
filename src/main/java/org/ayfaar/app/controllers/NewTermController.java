package org.ayfaar.app.controllers;

import org.ayfaar.app.utils.TermsMap;
import org.ayfaar.app.utils.TermsTaggingUpdater;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

@Controller
@RequestMapping("api/v2/term")
public class NewTermController {
    @Inject TermsMap termsMap;
    @Inject TermsTaggingUpdater taggingUpdater;
    @Inject AsyncTaskExecutor taskExecutor;

    @RequestMapping("{termName}/mark")
    public void mark(@PathVariable final String termName) {
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                taggingUpdater.update(termName);
            }
        });
    }
}
