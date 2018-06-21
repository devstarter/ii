package org.ayfaar.app.dao.impl;

import org.ayfaar.app.dao.VideoResourceDao;
import org.ayfaar.app.model.VideoResource;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.hibernate.criterion.Restrictions.like;

@Repository
public class VideoResourceDaoImpl extends AbstractHibernateDAO<VideoResource> implements VideoResourceDao {

    public VideoResourceDaoImpl() {
        super(VideoResource.class);
    }

    @Override
    public List<VideoResource> get(String nameOrCode, String year, Pageable pageable){

        Criteria criteria = criteria(pageable);

        if (nameOrCode != null && !nameOrCode.isEmpty())
            criteria.add(Restrictions.or(
                like("uri", nameOrCode, MatchMode.ANYWHERE),
                like("code", nameOrCode, MatchMode.ANYWHERE),
                like("title", nameOrCode, MatchMode.ANYWHERE)));

        if (year != null && !year.isEmpty()) criteria.add(like("code", year, MatchMode.ANYWHERE));

        return criteria.list();
    }
}
