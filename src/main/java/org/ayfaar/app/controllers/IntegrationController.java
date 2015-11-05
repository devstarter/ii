package org.ayfaar.app.controllers;

import org.apache.commons.lang.ArrayUtils;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.spring.Model;
import org.ayfaar.app.utils.RegExpUtils;
import org.ayfaar.app.utils.TermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

@Controller
@RequestMapping("api/integration")
public class IntegrationController {
    @Autowired
    TermService termService;
    @Autowired ItemDao itemDao;

    private List<String> allItemNumbers;
    private Map<String, List<Map.Entry<String, String>>> cache = new HashMap<String, List<Map.Entry<String, String>>>();
    private String[] ignoreTerms = {"интеллект", "чувство", "мысль", "воля", "время", "мир", "личность", "жизнь", "ген", "молекула", "атом", "элементарный", "форма", "окружающая действительность", "днк"};

    @Model
    @RequestMapping
    public Object t(@RequestBody String text, @RequestHeader("Referer") String referer, @RequestParam String id) {
        String cacheKey = referer+"#"+id;
        if (cache.containsKey(cacheKey)) return cache.get(cacheKey);

        Map<String, String> contains = new LinkedHashMap<String, String>();
        text = URLDecoder.decode(text).toLowerCase();

        // terms
        for (Map.Entry<String, TermService.TermProvider> entry : termService.getAll()) {
            String key = entry.getKey();
            TermService.TermProvider provider = entry.getValue();
            if (ArrayUtils.contains(ignoreTerms, provider.getName().toLowerCase())) continue;
            Matcher matcher = compile("((" + RegExpUtils.W + ")|^)" + key + "((" + RegExpUtils.W + ")|$)", Pattern.UNICODE_CHARACTER_CLASS).matcher(text);
            if (matcher.find()) {
                contains.put(key, provider.getName());
                text = text.replaceAll(key, "");
            }
        }
        // item numbers
        if (allItemNumbers == null) {
            allItemNumbers = itemDao.getAllNumbers();
        }
        for (String itemNumber : allItemNumbers) {
            Matcher matcher = compile("((" + RegExpUtils.W + ")|^|\\[)" + itemNumber + "((" + RegExpUtils.W + ")|$|\\])", Pattern.UNICODE_CHARACTER_CLASS).matcher(text);
            if (matcher.find()) {
                contains.put(itemNumber, itemNumber);
            }
        }

        final List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(contains.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return Integer.compare(o2.getKey().length(), o1.getKey().length());
            }
        });
        cache.put(cacheKey, list);
        return list.isEmpty() ? "" : list;
    }
}
