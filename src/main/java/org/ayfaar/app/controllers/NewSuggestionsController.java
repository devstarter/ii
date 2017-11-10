package org.ayfaar.app.controllers;

import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.services.ItemService;
import org.ayfaar.app.services.document.DocumentService;
import org.ayfaar.app.services.images.ImageService;
import org.ayfaar.app.services.moderation.UserRole;
import org.ayfaar.app.services.record.RecordService;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.services.videoResource.VideoResourceService;
import org.ayfaar.app.utils.*;
import org.ayfaar.app.utils.contents.ContentsUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;

@Slf4j
@RestController
@RequestMapping("api/suggestions")
public class NewSuggestionsController {

    @Inject TermService termService;
    @Inject TopicService topicService;
    @Inject ContentsService contentsService;
    @Inject DocumentService documentService;
    @Inject VideoResourceService videoResourceService;
    @Inject RecordService recordService;
    @Inject ItemService itemService;
    @Inject ContentsUtils contentsUtils;
    @Inject ImageService imageService;
    @Inject ArticleController articleController;
    @Inject SearchSuggestions searchSuggestions;
    @Inject CurrentUserProvider currentUserProvider;

    private List<String> escapeChars = Arrays.asList("(", ")", "[", "]", "{", "}");
    private static final int MAX_SUGGESTIONS = 5;
    private static final int MAX_WORDS_PARAGRAPH_AFTER_SEARCH = 4;

    @RequestMapping("term")
    @ResponseBody
    public Collection<String> suggestionTerms(@RequestParam String q) {
        return suggestions(q, true, false, false, false, false, false, false, false, false, false, false, false)
                .values();
    }

    public LinkedHashMap<String, String> suggestions(@RequestParam String q) {
        return suggestions(q,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true,  true);
    }

    @RequestMapping("all")
    @ResponseBody
    public LinkedHashMap<String, String> suggestions(@RequestParam String q,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_terms,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_topic,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_category_name,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_category_description,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_doc,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_video,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_video_code,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_item,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_record_name,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_record_code,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_articles,
                                           @RequestParam(required = false, defaultValue = "true") boolean with_images
    ) {
        LinkedHashMap<String, String> allSuggestions = new LinkedHashMap<>();
        List<Suggestions> items = new ArrayList<>();
        if (with_terms) items.add(Suggestions.TERMS); //default
        if (with_topic) items.add(Suggestions.TOPICS);
        if (with_category_name) items.add(Suggestions.CATEGORY_NAMES);
        if (with_category_description) items.add(Suggestions.CATEGORY_DESCRIPTIONS);
        if (with_doc) items.add(Suggestions.DOCUMENTS);
        if (with_video) items.add(Suggestions.VIDEOS);
        if (with_video_code) items.add(Suggestions.VIDEO_CODES);
        if (with_record_name) items.add(Suggestions.RECORD_NAMES);
        if (with_record_code) items.add(Suggestions.RECORD_CODES);
        if (with_item) items.add(Suggestions.ITEMS);
        if (with_articles) items.add(Suggestions.ARTICLES);
        if (with_images) items.add(Suggestions.IMAGES);
        for (Suggestions item : items) {
            Queue<String> queriesQueue = searchSuggestions.getQueue(q);
            List<Map.Entry<String, String>> suggestions = getSuggestions(queriesQueue, item);
            allSuggestions.putAll(searchSuggestions.getAllSuggestions(q,suggestions));
        }

        if (!currentUserProvider.getCurrentAccessLevel().accept(UserRole.ROLE_EDITOR)) {
            // remove duplications by values, but not for editors and admins
            Set<String> existing = new HashSet<>();
            allSuggestions = allSuggestions.entrySet()
                    .stream()
                    .filter(entry -> existing.add(entry.getValue().toLowerCase()))
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            Map.Entry::getValue,
                            (v1, v2) -> v1,
                            LinkedHashMap::new));
        }

        return allSuggestions;
    }

    private List<Map.Entry<String, String>> getSuggestions(Queue<String> queriesQueue, Suggestions item) {
        List<Map.Entry<String, String>> suggestions = new ArrayList<>();

        while (suggestions.size() < MAX_SUGGESTIONS && queriesQueue.peek() != null) {
            List<? extends Map.Entry<String, String>> founded = null;
            Map<String, String> mapUriWithNames = null;
            // fixme: в некоторых методах getAllUriNames при каждом вызове getSuggestions, происходит запрос в БД для получения всех имён, это не рационально, я бы сделал логику кеширования имён и обновления кеша в случае добавления/изменения видео или документа
            // или можно сделать RegExp запрос в БД и тогда не нужен кеш вовсе, просто из соображений скорости я стараюсь уменьшить запросы в БД
            switch (item) {
                case TERMS:
                    Collection<TermDao.TermInfo> allInfoTerms = termService.getAllInfoTerms();
                    final List<TermDao.TermInfo> suggested = searchSuggestions.getSuggested(queriesQueue.poll(), suggestions, allInfoTerms, TermDao.TermInfo::getName);
                    founded = StreamEx.of(suggested)
                            .map((i) -> new ImmutablePair<>(UriGenerator.generate(Term.class, i.getName()), i.getName()))
                            .toList();
                    break;
                case TOPICS:
                    mapUriWithNames = topicService.getAllUriNames();
                    break;
                case CATEGORY_NAMES:
                    mapUriWithNames = contentsService.getAllUriNames();
                    break;
                case CATEGORY_DESCRIPTIONS:
                    mapUriWithNames = contentsService.getAllUriDescription();
                    break;
                case DOCUMENTS:
                    mapUriWithNames = documentService.getAllUriNames();
                    break;
                case VIDEOS:
                    mapUriWithNames = videoResourceService.getAllUriNames();
                    break;
                case VIDEO_CODES:
                    mapUriWithNames = videoResourceService.getAllUriCodes();
                    break;
                case ITEMS:
                    mapUriWithNames = itemService.getAllUriNumbers();
                    break;
                case RECORD_NAMES:
                    mapUriWithNames = recordService.getAllUriNames();
                    break;
                case RECORD_CODES:
                    mapUriWithNames = recordService.getAllUriCodes();
                    break;
                case ARTICLES:
                    mapUriWithNames = articleController.getAllUriNames();
                    break;
                case IMAGES:
                    mapUriWithNames = imageService.getAllUriNames();
                    break;
            }
            if (item != Suggestions.TERMS)
                founded = searchSuggestions.getSuggested(queriesQueue.poll(), suggestions, mapUriWithNames.entrySet(), Map.Entry::getValue);

            if (item == Suggestions.ITEMS)
                Collections.reverse(founded);

            suggestions.addAll(founded.subList(0, min(MAX_SUGGESTIONS - suggestions.size(), founded.size())));
        }

        if (item == Suggestions.ITEMS) suggestions.sort(Comparator.comparing(Map.Entry::getValue));
        if (item == Suggestions.TERMS) suggestions.sort(Comparator.comparingInt(e -> e.getValue().length()));
        return suggestions;
    }
}