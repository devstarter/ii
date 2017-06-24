package org.ayfaar.app.utils.sitemap;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.services.topics.TopicService;
import org.ayfaar.app.utils.ContentsService;
import org.ayfaar.app.utils.TermService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.stream.Stream;


@Service
@Slf4j
public class URLGenerator {

    @Value("${site-base-url}")
    private String baseUrl;

    private TermService termsMap;
    private ItemDao itemDao;
    private ContentsService contentsService;
    private TopicService topicService;

    @Inject
    public URLGenerator(TermService termsMap, ItemDao itemDao, ContentsService contentsService, TopicService topicService) {
        this.termsMap = termsMap;
        this.itemDao = itemDao;
        this.contentsService = contentsService;
        this.topicService = topicService;
    }

    public StreamEx<String> getURLs(){
        return StreamEx.of(getTermsURL())
                .append(getItemsURL())
                .append(getCategoriesURL())
                .append(getParagraphsURL())
                .append(getTopicsURL());
    }

    private Stream<String> getTopicsURL(){
        return addBaseAndEncode(topicService.getAllNames().stream().map(s -> "t/" + s));
    }

    private Stream<String> getCategoriesURL(){
        return addBaseAndEncode(contentsService.getAllCategories().map(c -> "c/" + c.extractCategoryName()));
    }

    private Stream<String> getParagraphsURL(){
        return addBaseAndEncode(contentsService.getAllParagraphs().map(ContentsService.ParagraphProvider::code));
    }

    private Stream<String> getItemsURL(){
        return addBaseAndEncode(itemDao.getAllNumbers().stream());
    }

    private Stream<String> getTermsURL(){
        return addBaseAndEncode(termsMap.getAll().stream().map(Map.Entry::getKey));
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

    private Stream<String> addBaseAndEncode(Stream<String> strings){
        return strings.map(s -> baseUrl + "/" + encodeUrl(s));
    }

}
