package org.ayfaar.app.utils.sitemap;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.utils.CategoryService;
import org.ayfaar.app.utils.TermService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;
import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Slf4j
public class URLGenerator {

    @Value("${site-base-url}")
    private String baseUrl;

    private TermService termsMap;
    private ItemDao itemDao;
    private CategoryService categoryService;
    private TopicService topicService;

    @Inject
    public URLGenerator(TermService termsMap, ItemDao itemDao, CategoryService categoryService, TopicService topicService) {
        this.termsMap = termsMap;
        this.itemDao = itemDao;
        this.categoryService = categoryService;
        this.topicService = topicService;
    }

    public Stream<String> getURLs(){
        return Stream.of(getTermsURL(),
                getItemsURL(),
                getCategoriesURL(),
                getTopicsURL()).flatMap(Collection::stream);
    }

    private List<String> getTopicsURL(){
        return generateUrls(topicService.getAllNames().stream().map(s -> "t/" + s).collect(Collectors.toList()));
    }

    private List<String> getCategoriesURL(){
        return generateUrls(categoryService.getAll().values().stream().filter(categoryProvider -> !categoryProvider.isParagraph()).
                map(categoryProvider1 -> "c/" + categoryProvider1.extractCategoryName()).collect(Collectors.toList()));
    }

    private List<String> getItemsURL(){
        return generateUrls(itemDao.getAllNumbers());
    }

    private List<String> getTermsURL(){
        return generateUrls(termsMap.getAll().stream().map(Map.Entry::getKey).collect(Collectors.toList()));
    }

    private String encodeUrl(String term){
        String encodedUrl = null;
        try {
            encodedUrl = UriUtils.encodePath(term, Charsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("The Character Encoding is not supported.", e);
        }
        return encodedUrl;
    }

    private List<String> generateUrls(Collection<String> strings){
        return strings.stream().map(s -> baseUrl + "/" + encodeUrl(s)).collect(Collectors.toList());
    }

}
