package org.ayfaar.app.utils;

import lombok.extern.log4j.Log4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.ayfaar.app.annotations.Uri;
import org.ayfaar.app.model.*;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.internal.SessionImpl;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.stream.Stream;

import static org.ayfaar.app.utils.StreamUtils.single;

@Log4j
public class UriGenerator implements IdentifierGenerator {
    public static String generate(UID object) throws HibernateException {
        return (String) new UriGenerator().generate(null, object);
    }

    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        Uri annotation = object.getClass().getAnnotation(Uri.class);
        String nameSpace = annotation.nameSpace();
        if (object instanceof ItemsRange) {
            return nameSpace + ((ItemsRange) object).getCode();
        }
        if (object instanceof HasSequence) {
            Sequence sequence = null;
            try {
                sequence = (Sequence) ((HasSequence) object).getSequence().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                log.error(e);
            }
            ((SessionImpl) session).getSessionFactory().getCurrentSession().save(sequence);
            return nameSpace + sequence.getSeq();
        }
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

    @SuppressWarnings("unchecked")
    public static Class<? extends UID> getClassByUri(String uri) {
        return Stream.of(Item.class, Article.class, Category.class, ItemsRange.class, Resource.class, VideoResource.class, Term.class, Topic.class, Document.class, Record.class)
                .filter(clazz -> {
                    Uri annotation = clazz.getAnnotation(Uri.class);
                    Assert.notNull(annotation, "Uri annotation not found for class "+clazz);
                    return uri.startsWith(annotation.nameSpace());
                }).collect(single()).orElseThrow(() -> new RuntimeException("Has no class for uri "+uri));
    }
}