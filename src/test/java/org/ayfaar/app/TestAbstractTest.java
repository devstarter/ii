package org.ayfaar.app;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by konstantin_k on 01.08.2014.
 */
public class TestAbstractTest extends AbstractTest {

    @Test
    public void testAssertListEquals(){

        class ThreeValue{
            public boolean result;
            public List<String> expected;
            public List<String> actual;

            private ThreeValue(boolean result, List<String> expected, List<String> actual) {
                this.result = result;
                this.expected = expected;
                this.actual = actual;
            }

            @Override
            public String toString() {
                return "ThreeValue{" +
                        "result=" + result +
                        ", expected=" + (expected==null ? "null" : Arrays.toString(expected.toArray())) +
                        ", actual=" + (actual==null ? "null" : Arrays.toString(actual.toArray())) +
                        "}";
            }
        }

        List<String> expected = asList("1","2","3","4","5");
        List<ThreeValue> testList = asList(
                 new ThreeValue(true,  expected, asList("1", "2", "5", "4", "3"))
                ,new ThreeValue(true,  expected, asList("5","4","3","2","1"))
                ,new ThreeValue(true,      null, null)
                ,new ThreeValue(false,     null, asList("5","4","3","2","1"))
                ,new ThreeValue(false, expected, null)
                ,new ThreeValue(false, expected, asList("5","4","3","2"))
                ,new ThreeValue(false, expected, asList("5","4","3","2","6"))
                ,new ThreeValue(false, expected, asList("1","2","3","4","4"))
        );

        for (ThreeValue tree:testList){
            if(tree.result){
                assertListEquals(tree.toString(),tree.expected,tree.actual);
            }else{
                assertListNotEquals(tree.toString(),tree.expected,tree.actual);
            }
        }
    }
}
