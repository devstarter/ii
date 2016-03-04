package org.ayfaar.app.utils.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

public class EnumHibernateType implements UserType, ParameterizedType {
    public static final String ENUM = "enum";
    public static final String CLASS = "enumClass";

    private static final int[] VARCHAR_SQL_TYPE = new int[]{ Types.VARCHAR };
    private static final int[] CHAR_SQL_TYPE = new int[]{ Types.CHAR };
    private Class<? extends Enum> enumClass;
    private boolean valueMode;
    private Method getter;
    private Method generator;

    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty(CLASS);

        try {
            enumClass = Class.forName(enumClassName).asSubclass(Enum.class);
            valueMode = ValueEnum.class.isAssignableFrom(enumClass);
            if (valueMode) {
                getter = enumClass.getMethod("getValue");
                generator = enumClass.getMethod("getEnum", String.class);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Enum) value;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public boolean isMutable() {
        return false;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
        String value = rs.getString(names[0]);
        if (rs.wasNull() || value == null) {
            return null;
        }
        try {
            return /*generator != null ? */generator.invoke(enumClass, value)/* : Enum.valueOf(enumClass, value)*/;
        } catch (Exception e) {
            throw new HibernateException("invalid enum " + enumClass + " value " + value, e);
        }
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, sqlTypes()[0]);
        } else {
            try {
                st.setString(index, getter != null ? getter.invoke(value).toString() : ((Enum) value).name());
            } catch (Exception e) {
                throw new HibernateException("invalid enum " + enumClass + " object " + value, e);
            }
        }
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    public Class returnedClass() {
        return enumClass;
    }

    public int[] sqlTypes() {
        return valueMode ? CHAR_SQL_TYPE : VARCHAR_SQL_TYPE;
    }
}