package org.ayfaar.app.utils;

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
        return str[0];
    }
}
