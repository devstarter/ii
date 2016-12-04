package org.ayfaar.app.utils;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.utils.contents.ContentsUtils;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CASE;

@Slf4j
@Service
public class SearchSuggestions {

    private List<String> escapeChars = Arrays.asList("(", ")", "[", "]", "{", "}");

    public Queue<String> getQueue(String q) {
        q = q.replace("*", ".*");
        q = q.replaceAll("\\s+", ".*");
        q = escapeRegexp(q);
        q = addDuplications(q);
        return new LinkedList<>(asList(
                "^" + q,
                "[\\s\\-]" + q,
                q
        ));
    }


    protected static String addDuplications(String q) {
        return q.replaceAll("([A-Za-zА-Яа-яЁё])", "$1+-*$1*");
    }

    public <T> List<T> getSuggested(String query, List<Map.Entry<String, String>> suggestions, Collection<T> uriWithNames, Function<T, String> nameProvider) {
        List<T> list = new ArrayList<>();
        Pattern pattern = Pattern.compile(query, CASE_INSENSITIVE + UNICODE_CASE);
        for (T entry : uriWithNames) {
            String name = nameProvider.apply(entry);
            Matcher matcher = pattern.matcher(name);
            if (matcher.find() && !suggestions.contains(name) && !list.contains(name)) {
                list.add(entry);
            }
        }
        Collections.reverse(list);
        return list;
    }

    private String escapeRegexp(String query) {
        for (String bracket : escapeChars) {
            if (query.contains(bracket)) {
                query = query.replace(bracket, "\\" + bracket);
            }
        }
        return query;
    }

    public Map<String, String> getAllSuggestions(String q, List<Map.Entry<String, String>> suggestions){
        Map<String, String> allSuggestions = new LinkedHashMap<>();

        for (Map.Entry<String, String> suggestion : suggestions) {
            String key = suggestion.getKey();
            String value = suggestion.getValue();
            if(key.contains("ии:пункты:")) {
                String suggestionParagraph = ContentsUtils.splitToSentence(value, q);
                if(!Objects.equals(suggestionParagraph, ""))allSuggestions.put(key, suggestionParagraph);
            }
            else allSuggestions.put(key, value);
        }
        return allSuggestions;
    }
}
