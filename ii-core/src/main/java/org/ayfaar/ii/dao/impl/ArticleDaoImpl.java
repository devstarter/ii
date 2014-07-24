package org.ayfaar.ii.dao.impl;

import org.ayfaar.ii.dao.ArticleDao;
import org.ayfaar.ii.model.Article;
import org.ayfaar.ii.model.ArticleSeq;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ArticleDaoImpl extends AbstractHibernateDAO<Article> implements ArticleDao {

    public ArticleDaoImpl() {
        super(Article.class);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Article save(Article article) {
        if (article.getId() == null) {
            ArticleSeq articleSeq = new ArticleSeq();
            currentSession().save(articleSeq);
            article.setId(articleSeq.getSeq());
        }
        currentSession().saveOrUpdate(article);
        return article;
    }
}
