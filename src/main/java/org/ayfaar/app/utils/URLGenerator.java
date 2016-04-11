package org.ayfaar.app.utils;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.ListUtils;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.services.topics.TopicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Value("${OPENSHIFT_BASE_URL}")
    private String baseUrl;

    @Inject
    TermService termsMap;

    @Inject
    ItemDao itemDao;

    @Inject
    CategoryService categoryService;

    @Inject
    TopicService topicService;

    public List<String> getURLs(){
        return Stream.of(getTermsURL(), getItemsURL(), getCategoriesURL(),getTopicsURL()).
                flatMap(x -> x.stream()).collect(Collectors.toList());
    }

    public List<String> getTopicsURL(){
        return generateUrls(topicService.getAllNames().stream().map(s -> "t/" + s).collect(Collectors.toList()));
    }

    public List<String> getCategoriesURL(){
        return generateUrls(categoryService.getAll().keySet().stream().map(s -> "c/" + s).collect(Collectors.toList()));
    }

    public List<String> getItemsURL(){
        return generateUrls(itemDao.getAllNumbers());
    }

    public List<String> getTermsURL(){
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
        return strings.stream().map(s -> baseUrl + encodeUrl(s)).collect(Collectors.toList());
    }

}
