package org.ayfaar.app.utils;

import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class URLGeneratorImpl implements UrlGenerator{

    @Inject
    TermServiceImpl termsMap;

    private List<String> getTerms(){
        List<String> list = new ArrayList<>();

        for (Map.Entry<String, TermService.TermProvider> entry : termsMap.getAll())  {
            String key = entry.getKey();
            list.add(key);
        }
        return list;
    }

    private String encodeUrl(String term){
        String encodedUrl = null;
        try {
            encodedUrl = URLEncoder.encode(term, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedUrl;
    }

    public List<String> generateTermsUrls(){
        List<String> terms = getTerms();
        List<String> list = new ArrayList<>();
        String termUrl;
        for (String term : terms){
            termUrl = "http://ii.ayfaar.org/" + encodeUrl(term);
            list.add(termUrl);
        }
        return list;
    }
}
