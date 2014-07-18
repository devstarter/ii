package org.ayfaar.app.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.ayfaar.app.annotations.Uri;
import org.ayfaar.app.model.UID;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

//@Component
public class UriGenerator implements IdentifierGenerator {
    public static String generate(UID object) throws HibernateException {
        return (String) new UriGenerator().generate(null, object);
    }

    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        Uri annotation = object.getClass().getAnnotation(Uri.class);
        String nameSpace = annotation.nameSpace();
        String uri = null;
        try {
            Object property = PropertyUtils.getProperty(object, annotation.field());
            uri = nameSpace + property;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return uri;
    }

    public static String generate(Class clazz, String id) {
        Uri annotation = (Uri) clazz.getAnnotation(Uri.class);
        return annotation.nameSpace() + id;
    }

    public static String getValueFromUri(Class objectClass, String uri) {
        Uri annotation = (Uri) objectClass.getAnnotation(Uri.class);
        return uri.replace(annotation.nameSpace(), "");
    }
}