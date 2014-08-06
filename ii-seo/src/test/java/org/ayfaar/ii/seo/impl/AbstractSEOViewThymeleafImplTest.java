package org.ayfaar.ii.seo.impl;

import org.ayfaar.ii.seo.SEOView;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class AbstractSEOViewThymeleafImplTest {

    private class SEOViewThymeleafImplMock extends AbstractSEOViewThymeleafImpl{

        public SEOViewThymeleafImplMock(String name){
            super(name);
        }

        @Override
        protected Map<String, ?> getParameters(Map<String, String> viewParameters) {
            Map<String, String> parameters = new HashMap<>(viewParameters);
            parameters.put("param3","value3");
            return parameters;
        }
    }

    @Test
    public void testGetHTML() throws Exception {
        Map<String, String> viewParameters = new HashMap<String,String>(){{
            put("param1","value1");
            put("param2","value2");
        }};

        SEOViewThymeleafImplMock.setPREFIX("ii-seo\\src\\test\\java\\org\\ayfaar\\ii\\seo\\impl\\templates\\");
        SEOView view = new SEOViewThymeleafImplMock("testView");
        view.setViewParameters(viewParameters);
        String result = view.getHTML();

        assertTrue("Title",result.contains("<title>Test view</title>"));
        assertTrue("Value 1",result.contains("<p>value1</p>"));
        assertTrue("Value 2",result.contains("<p>value2</p>"));
        assertTrue("Value 3",result.contains("<p>value3</p>"));
        assertFalse("Param 1",result.contains("<p>param1</p>"));
        assertFalse("Param 2",result.contains("<p>param2</p>"));
        assertFalse("Param 3",result.contains("<p>param3</p>"));
    }
}