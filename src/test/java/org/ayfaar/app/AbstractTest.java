package org.ayfaar.app;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.List;
import static org.junit.Assert.*;

public abstract class AbstractTest {

    public String getFile(String fileName) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(fileName));
    }

    private <T> boolean isListEquals(List<T> expected, List<T> actual){
        if(expected == null || actual == null) {
            return (expected == actual);
        }

        if(expected.size() == actual.size()){
            for(int i = 0; i < expected.size();i++)
                if(!actual.contains(expected.get(i)))
                    return false;

            return true;
        } else {
            return false;
        }
    }


    public <T> void assertListEquals(String message, List<T> expected, List<T> actual){
        if(!isListEquals(expected,actual)){
            fail(message);
        }
    }

    public <T> void assertListNotEquals(String message, List<T> expected, List<T> actual){
        if(isListEquals(expected,actual)){
            fail(message);
        }
    }

    public <T> void assertListEquals(List<T> expected, List<T> actual){
        assertListEquals(null,expected,actual);
    }

    public <T> void assertListNotEquals(List<T> expected, List<T> actual){
        assertListNotEquals(null, expected, actual);
    }

}
