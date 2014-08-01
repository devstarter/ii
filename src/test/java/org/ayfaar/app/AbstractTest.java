package org.ayfaar.app;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;
import static org.junit.Assert.*;

public abstract class AbstractTest {

    public String getFile(String fileName) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(fileName));
    }

    private static <T> boolean isListEqualsIgnoreOrder(List<T> expected, List<T> actual){
        if(expected == null || actual == null) {
            return (expected == actual);
        }

        if(expected.size() == actual.size()){
            for (T item : expected) {
                if (!actual.contains(item)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }


    public static <T> void assertListEqualsIgnoreOrder(String message, List<T> expected, List<T> actual){
        if(!isListEqualsIgnoreOrder(expected, actual)){
            fail(message);
        }
    }

    public static <T> void assertListNotEqualsIgnoreOrder(String message, List<T> expected, List<T> actual){
        if(isListEqualsIgnoreOrder(expected, actual)){
            fail(message);
        }
    }

    public static <T> void assertListEqualsIgnoreOrder(List<T> expected, List<T> actual){
        assertListEqualsIgnoreOrder(null, expected, actual);
    }

    public static <T> void assertListNotEqualsIgnoreOrder(List<T> expected, List<T> actual){
        assertListNotEqualsIgnoreOrder(null, expected, actual);
    }

}
