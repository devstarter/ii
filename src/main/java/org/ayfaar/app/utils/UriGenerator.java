package org.ayfaar.app.utils;

import org.ayfaar.app.model.UID;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class UriGenerator implements IdentifierGenerator {
        @Override
        public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
            UID uid = (UID) object;
            return uid.generateUri();
        }
    }