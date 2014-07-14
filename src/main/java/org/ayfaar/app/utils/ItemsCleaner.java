package org.ayfaar.app.utils;

<<<<<<< HEAD
public class ItemsCleaner {
    public static String clean(String value) {
        String[] str = value.split("\n");

        if(value.length() == 0) {
            return null;
        }
=======
import org.apache.commons.lang.NotImplementedException;
import org.ayfaar.app.dao.ItemDao;
import org.springframework.beans.factory.annotation.Autowired;

public class ItemsCleaner {
    @Autowired
    ItemDao itemDao;

    public static String clean(String value) {
        // fixme: issue#2
        //throw new NotImplementedException("issue#2");

        String[] str = value.split("\n");
>>>>>>> 7f23c6e7e979eb5673a20ed474e7add63584dbbe
        return str[0];
    }
}
