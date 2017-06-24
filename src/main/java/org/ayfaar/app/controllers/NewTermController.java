package org.ayfaar.app.controllers;

import org.ayfaar.app.utils.RegExpUtils;
import org.ayfaar.app.utils.TermService;
import org.ayfaar.app.utils.TermServiceImpl;
import org.ayfaar.app.utils.TermsTaggingUpdater;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.sort;
import static java.util.regex.Pattern.compile;

@RestController
@RequestMapping("api/v2/term")
public class NewTermController {
    @Inject TermServiceImpl termsMap;
    @Inject TermsTaggingUpdater taggingUpdater;
    @Inject AsyncTaskExecutor taskExecutor;
    @Inject SuggestionsController suggestionsController;

    @RequestMapping("{termName}/mark")
    public void mark(@PathVariable final String termName) {
        taskExecutor.submit(() -> taggingUpdater.update(termName));
    }

    @RequestMapping(value = "get-terms-in-text", method = RequestMethod.POST)
    public Object getTerms(@RequestParam String text) {
        Map<String, Integer> contains = new HashMap<>();
        text = text.toLowerCase();

        for (Map.Entry<String, TermService.TermProvider> entry : termsMap.getAll()) {
            String key = entry.getKey();
            Matcher matcher = compile("((" + RegExpUtils.W + ")|^)" + key
                    + "((" + RegExpUtils.W + ")|$)", Pattern.UNICODE_CHARACTER_CLASS)
                    .matcher(text);
            if (matcher.find()) {
                int count = 1;
                while (matcher.find()) count++;
                contains.put(entry.getValue().getName(), count);
                text = text.replaceAll(key, "");
            }
        }

        final List<Map.Entry<String, Integer>> sorted = new ArrayList<Map.Entry<String, Integer>>(contains.entrySet());
        sort(sorted, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        return sorted;
    }

    @RequestMapping("suggest")
    public List<String> suggest(@RequestParam String q) {
        return suggestionsController.suggestions(q);
    }
}
