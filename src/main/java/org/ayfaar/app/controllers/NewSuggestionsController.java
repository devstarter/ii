package org.ayfaar.app.controllers;

import one.util.streamex.StreamEx;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.services.document.DocumentService;
import org.ayfaar.app.services.record.RecordService;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.services.videoResource.VideoResourceService;
import org.ayfaar.app.utils.ContentsService;
import org.ayfaar.app.utils.TermService;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CASE;

@RestController
@RequestMapping("api/suggestions")
public class NewSuggestionsController {

    @Autowired TermService termService;
    @Autowired TopicService topicService;
    @Autowired ContentsService contentsService;
    @Autowired DocumentService documentService;
    @Autowired VideoResourceService videoResourceService;
    @Autowired RecordService recordService;

    private List<String> escapeChars = Arrays.asList("(", ")", "[", "]", "{", "}");
    private static final int MAX_SUGGESTIONS = 5;

    @RequestMapping("all")
    @ResponseBody
    public Map<String, String> suggestions(@RequestParam String q,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_terms,
                                           @RequestParam(required = false) boolean with_topic,
                                           @RequestParam(required = false) boolean with_category,
                                           @RequestParam(required = false) boolean with_doc,
                                           @RequestParam(required = false) boolean with_video,
                                           @RequestParam(required = false) boolean with_record_name,
                                           @RequestParam(required = false) boolean with_record_code
    ) {
        Map<String, String> allSuggestions = new LinkedHashMap<>();
        List<Suggestions> items = new ArrayList<>();
        if (with_terms) items.add(Suggestions.TERM); //default
        if (with_topic) items.add(Suggestions.TOPIC);
        if (with_category) items.add(Suggestions.CATEGORY);
        if (with_doc) items.add(Suggestions.DOCUMENT);
        if (with_video) items.add(Suggestions.VIDEO);
        if (with_record_name) items.add(Suggestions.RECORD_NAME);
        if (with_record_code) items.add(Suggestions.RECORD_CODE);
        for (Suggestions item : items) {
            Queue<String> queriesQueue = getQueue(q);
            for (Map.Entry<String, String> suggestion : getSuggestions(queriesQueue, item)) {
                allSuggestions.put(suggestion.getKey(), suggestion.getValue());
            }
        }
        return allSuggestions;
    }

    private Queue<String> getQueue(String q) {
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

    private List<Map.Entry<String, String>> getSuggestions(Queue<String> queriesQueue, Suggestions item) {
        List<Map.Entry<String, String>> suggestions = new ArrayList<>();

        while (suggestions.size() < MAX_SUGGESTIONS && queriesQueue.peek() != null) {
            List<? extends Map.Entry<String, String>> founded = null;
            Map<String, String> mapUriWithNames = null;
            // fixme: в некоторых методах getAllUriNames при каждом вызове getSuggestions, происходит запрос в БД для получения всех имён, это не рационально, я бы сделал логику кеширования имён и обновления кеша в случае добавления/изменения видео или документа
            // или можно сделать RegExp запрос в БД и тогда не нужен кеш вовсе, просто из соображений скорости я стараюсь уменьшить запросы в БД
            switch (item) {
                case TERM:
                    Collection<TermDao.TermInfo> allInfoTerms = termService.getAllInfoTerms();
                    final List<TermDao.TermInfo> suggested = getSuggested(queriesQueue.poll(), suggestions, allInfoTerms, TermDao.TermInfo::getName);
                    founded = StreamEx.of(suggested)
                            .map((i) -> new ImmutablePair<>(UriGenerator.generate(Term.class, i.getName()), i.getName()))
                            .toList();
                    break;
                case TOPIC:
                    mapUriWithNames = topicService.getAllUriNames();
                    break;
                case CATEGORY:
                    mapUriWithNames = contentsService.getAllUriNames();
                    break;
                case DOCUMENT:
                    mapUriWithNames = documentService.getAllUriNames();
                    break;
                case VIDEO:
                    mapUriWithNames = videoResourceService.getAllUriNames();
                    break;
                case RECORD_NAME:
                    mapUriWithNames = recordService.getAllUriNames();
                    break;
                case RECORD_CODE:
                    mapUriWithNames = recordService.getAllUriCodes();
                    break;
            }
            if (item != Suggestions.TERM)
                founded = getSuggested(queriesQueue.poll(), suggestions, mapUriWithNames.entrySet(), Map.Entry::getValue);

            suggestions.addAll(founded.subList(0, min(MAX_SUGGESTIONS - suggestions.size(), founded.size())));
        }

        Collections.sort(suggestions, (e1, e2) -> Integer.valueOf(e1.getValue().length()).compareTo(e2.getValue().length()));
        return suggestions;
    }

    protected static String addDuplications(String q) {
        return q.replaceAll("([A-Za-zА-Яа-яЁё])", "$1+-*$1*");
    }

    private <T> List<T> getSuggested(String query, List<Map.Entry<String, String>> suggestions, Collection<T> uriWithNames, Function<T, String> nameProvider) {
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
}
