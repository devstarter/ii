package org.ayfaar.app.controllers;

import org.ayfaar.app.utils.TermsMap;
import org.ayfaar.app.utils.TermsTaggingUpdater;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;

@Controller
@RequestMapping("api/v2/term")
public class NewTermController {
    @Inject TermsMap termsMap;
    @Inject TermsTaggingUpdater taggingUpdater;

    @RequestMapping("{termName}/mark")
    public void mark(@PathVariable String termName) {
        taggingUpdater.update(termName);
    }
}
