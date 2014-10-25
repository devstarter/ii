package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.ArticleDao;
import org.ayfaar.app.model.Article;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.AliasesMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.springframework.util.Assert.notNull;

@Controller
@RequestMapping("api/article")
public class ArticleController {

    @Autowired ArticleDao articleDao;
    @Autowired AliasesMap aliasesMap;


    @RequestMapping("{id}")
    @ResponseBody
    public Article get(@PathVariable Integer id) {
        Article article = articleDao.get("id", id);
        notNull(article, "Article not found");
        return article;
    }

    @RequestMapping("{id}/related-terms")
    @ResponseBody
    public List<Term> getRelatedTerms(@PathVariable Integer id) {
        Article article = articleDao.get("id", id);
        notNull(article, "Article not found");
        return aliasesMap.findTermsInside(article.getName()+" "+article.getContent());
    }
}
