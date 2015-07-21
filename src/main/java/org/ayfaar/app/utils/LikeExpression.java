package org.ayfaar.app.utils;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.TypedValue;

// http://stackoverflow.com/a/1111801/975169
public class LikeExpression implements Criterion {
    private final String propertyName;
    private final String value;
    private final Character escapeChar;

    protected LikeExpression(
            String propertyName,
            Object value) {
        this(propertyName, value.toString(), (Character) null);
    }

    protected LikeExpression(
            String propertyName,
            String value,
            MatchMode matchMode) {
        this( propertyName, matchMode.toMatchString( value
                .toString()
                .replaceAll("!", "!!")
                .replaceAll("%", "!%")
                .replaceAll("_", "!_")), '!' );
    }

    protected LikeExpression(
            String propertyName,
            String value,
            Character escapeChar) {
        this.propertyName = propertyName;
        this.value = value;
        this.escapeChar = escapeChar;
    }

    public String toSqlString(
            Criteria criteria,
            CriteriaQuery criteriaQuery) throws HibernateException {
        Dialect dialect = criteriaQuery.getFactory().getDialect();
        String[] columns = criteriaQuery.getColumnsUsingProjection( criteria, propertyName );
        if ( columns.length != 1 ) {
            throw new HibernateException( "Like may only be used with single-column properties" );
        }
        String lhs = lhs(dialect, columns[0]);
        return lhs + " like ?" + ( escapeChar == null ? "" : " escape ?" );

    }

    public TypedValue[] getTypedValues(
            Criteria criteria,
            CriteriaQuery criteriaQuery) throws HibernateException {
        return new TypedValue[] {
                criteriaQuery.getTypedValue( criteria, propertyName, typedValue(value) ),
                criteriaQuery.getTypedValue( criteria, propertyName, escapeChar.toString() )
        };
    }

    protected String lhs(Dialect dialect, String column) {
        return column;
    }

    protected String typedValue(String value) {
        return value;
    }

    public static Criterion like(String propertyName, Object value) {
        return new LikeExpression(propertyName, value);
    }

    public static Criterion like(String propertyName, String value, MatchMode matchMode) {
        return new LikeExpression(propertyName, value, matchMode);
    }

    public static Criterion like(String propertyName, String value, Character escapeChar) {
        return new LikeExpression(propertyName, value, escapeChar);
    }
}