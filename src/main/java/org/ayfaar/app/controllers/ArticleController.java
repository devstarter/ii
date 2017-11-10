package org.ayfaar.app.controllers;

import lombok.extern.slf4j.Slf4j;
import org.ayfaar.app.dao.ArticleDao;
import org.ayfaar.app.model.Article;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.TermServiceImpl;
import org.ayfaar.app.utils.TermsMarker;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.Assert.notNull;

@Slf4j
@Controller
@RequestMapping("api/article")
public class ArticleController {

    @Autowired ArticleDao articleDao;
    @Autowired TermServiceImpl aliasesMap;
    @Autowired TermsMarker termsMarker;
    @Autowired Mapper mapper;

    private Map<String, String> uriNames;

    @PostConstruct
    private void init() {
        log.info("Articles (uri-names) loading...");
        uriNames = articleDao.getAll().stream().collect(Collectors.toMap(Article::getUri, a -> "9 том: " + a.getName()));
        log.info("Articles loaded");
    }

    @RequestMapping("{id}")
    @ResponseBody
    public Article get(@PathVariable Integer id) {
        Article article = articleDao.get("id", id);
        notNull(article, "Article not found");
        if (article.getTaggedContent() == null || article.getTaggedContent().isEmpty()) {
            article.setTaggedContent(termsMarker.mark(article.getContent()));
            articleDao.save(article);
        }

        Article presenter = new Article();
        mapper.map(article, presenter);
        presenter.setName("9 том: " + article.getName());
        presenter.setTaggedContent(null);
        presenter.setContent(article.getTaggedContent());
        return presenter;
    }

    @RequestMapping("{id}/related-terms")
    @ResponseBody
    public List<Term> getRelatedTerms(@PathVariable Integer id) {
        Article article = articleDao.get("id", id);
        notNull(article, "Article not found");
        return aliasesMap.findTermsInside(article.getName()+" "+article.getContent());
    }

    public Map<String, String> getAllUriNames() {
        return uriNames;
    }
}
