package org.ayfaar.app.controllers;

import org.ayfaar.app.spring.Model;
import org.ayfaar.app.utils.RegExpUtils;
import org.ayfaar.app.utils.TermsMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

@Controller
@RequestMapping("api/integration")
public class IntegrationController {
    @Autowired TermsMap termsMap;

    @RequestMapping
    @Model
    public Object t(@RequestBody String text) {
        Map<String, String> contains = new LinkedHashMap<String, String>();
        text = URLDecoder.decode(text).toLowerCase();

        for (Map.Entry<String, TermsMap.TermProvider> entry : termsMap.getAll()) {
            String key = entry.getKey();
            Matcher matcher = compile("((" + RegExpUtils.W + ")|^)" + key
                    + "((" + RegExpUtils.W + ")|$)", Pattern.UNICODE_CHARACTER_CLASS)
                    .matcher(text);
            if (matcher.find()) {
                contains.put(key, entry.getValue().getName());
                text = text.replaceAll(key, "");
            }
        }

        final List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(contains.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return Integer.compare(o2.getKey().length(), o1.getKey().length());
            }
        });
        return list;
    }
}
