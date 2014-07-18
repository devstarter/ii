package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.ArticleDao;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Article;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.spring.Model;
import org.ayfaar.app.utils.AliasesMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.springframework.util.Assert.notNull;

@Controller
@RequestMapping("article")
public class ArticleController {

    @Autowired CommonDao commonDao;
    @Autowired ItemDao itemDao;
    @Autowired TermDao termDao;
    @Autowired TermController termController;
    @Autowired AliasesMap aliasesMap;
    @Autowired ArticleDao articleDao;


    @Model
    @ResponseBody
    @RequestMapping("{id}")
    public Article get(@PathVariable Integer id) {
        Article article = articleDao.get("id", id);
        notNull(article, "Article not found");
        return article;
    }

    @RequestMapping("{id}/related-terms")
    @Model
    @ResponseBody
    public List<Term> getRelatedTerms(@PathVariable Integer id) {
        Article article = articleDao.get("id", id);
        notNull(article, "Article not found");
        return aliasesMap.findTermsInside(article.getName()+" "+article.getContent());
    }
}
