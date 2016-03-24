package org.ayfaar.app.utils;

import com.google.common.base.Charsets;
import org.apache.commons.collections.ListUtils;
import org.ayfaar.app.dao.ItemDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;
import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class URLGenerator {

    private static final Logger logger = LoggerFactory.getLogger(URLGenerator.class);

    @Value("${OPENSHIFT_BASE_URL}")
    private String baseUrl;

    @Inject
    TermService termsMap;

    @Inject
    ItemDao itemDao;

    @Inject
    CategoryService categoryService;

    public List<String> getURLs(){
        List unionList = ListUtils.union(getTermsURL(), getItemsURL());
        return ListUtils.union(unionList,getCategoriesURL());
    }

    public List<String> getCategoriesURL(){
        List<String> listCategory = new ArrayList<>();
        categoryService.getAll().keySet().stream().forEach(s -> listCategory.add("c/"+s));
        return generateUrls(listCategory);
    }

    public List<String> getItemsURL(){
        return generateUrls(itemDao.getAllNumbers());
    }

    public List<String> getTermsURL(){
        List<String> strings = termsMap.getAll().stream().map(Map.Entry::getKey).collect(Collectors.toList());
        return generateUrls(strings);
    }

    private String encodeUrl(String term){
        String encodedUrl = null;
        try {
            encodedUrl = UriUtils.encodePath(term, Charsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            logger.error("Exception", e);
        }
        return encodedUrl;
    }

    private List<String> generateUrls(Collection<String> strings){
        List<String> list = new ArrayList<>();
        strings.stream().forEach(s -> list.add(baseUrl + encodeUrl(s)));
        return list;
    }

}
