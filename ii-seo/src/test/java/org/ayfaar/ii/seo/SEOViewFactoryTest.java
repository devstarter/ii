package org.ayfaar.ii.seo;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.apache.commons.lang.NotImplementedException;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static java.util.Arrays.asList;

public class SEOViewFactoryTest {

    SEOViewFactory factory;

    @Before
    public void setUp() throws Exception {
        Constructor cons = SEOViewFactory.class.getConstructor(new Class[0]);
        cons.setAccessible(true);
        factory = (SEOViewFactory)cons.newInstance();
    }

    private String getMockHTML(Map<String, String> viewParameters){
        StringBuilder result = new StringBuilder();
        result.append("<html>");
        for (Map.Entry<String,String> item : viewParameters.entrySet()){
            result.append(item.getKey()).append(" = ").append(item.getValue()).append("\n");
        }
        result.append("</html>");
        return result.toString();
    }

    private class MockSOEView implements SEOView {
        private Map<String, String> viewParameters;

        MockSOEView(Map<String, String> viewParameters) {
            this.viewParameters = viewParameters;
        }

        @Override
        public void setViewParameters(Map<String, String> viewParameters) {
            this.viewParameters = new HashMap<>(viewParameters);
        }

        @Override
        public String getHTML() {
            return getMockHTML(viewParameters);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MockSOEView that = (MockSOEView) o;

            return viewParameters.equals(that.viewParameters);
        }
    }

    @Test
    public void testSetGetViews(){
        Map<String,String> testParam1 = new HashMap<String,String>(){{
            put("param1","test1");
        }};
        Map<String,String> testParam2 = new HashMap<String,String>(){{
            put("param2","test2");
        }};

        final SEOView view1 = new MockSOEView(testParam1);
        final SEOView view2 = new MockSOEView(testParam2);

        Map<String,SEOView> viewList = new HashMap<String,SEOView>(){{
            put("view1",view1);
            put("view2",view2);
        }};

        factory.setViews(viewList);

        assertEquals(factory.getView("view1"), view1);
        assertEquals(factory.getView("view2"), view2);
        assertNull(factory.getView("view3"));
    }

    @Test
    public void testNotIsCached(){
        Map<String,String> testMap = new HashMap<String, String>(){{
            put("test1","test2");
        }};

        assertFalse(factory.isCached("test",new HashMap<>(testMap)));

        factory.cachingView("test",new HashMap<>(testMap),"hmtlsssss");

        assertFalse(factory.isCached("test1",new HashMap<>(testMap)));
        assertTrue(factory.isCached("test", new HashMap<>(testMap)));
    }

    @Test
    public void testCachedView(){
        class Cache{
            public String viewName;
            public Map<String,String> viewParameters;
            public String html;

            Cache(String viewName, Map<String, String> viewParameters, String html) {
                this.viewName = viewName;
                this.viewParameters = viewParameters;
                this.html = html;
            }
        }

        List<Cache> testCache = asList(
                new Cache("view0",new HashMap<String, String>(),"<div>wiebfwqwdqeowefowe</div>")
                ,new Cache("view1",new HashMap<String, String>(){{
                    put("param1","value1");
                }},"<div>qqq</div>")
                ,new Cache("view2",new HashMap<String, String>(){{
                    put("param1","value1");
                    put("param2","value2");
                }},"<div>1231231</div>")
                ,new Cache("view3",new HashMap<String, String>(){{
                    put("param1","value2");
                    put("param2","value1");
                }},"<div>dqwdqwd</div>")
                ,new Cache("view4",new HashMap<String, String>(){{
                    put("param1","value2");
                    put("param2","value1");
                }},"<div>qqddddqqqqq</div>")
        );

        for(Cache item:testCache){
            factory.cachingView(item.viewName,item.viewParameters,item.html);
        }

        for(Cache item:testCache){
            assertTrue(factory.isCached(item.viewName,item.viewParameters));
            assertEquals(item.html,factory.getCachedView(item.viewName,item.viewParameters));
        }
    }

    @Test
    public void testGetHtml(){
        Map<String,String> testParam1 = new HashMap<String,String>(){{
            put("param1","test1");
        }};
        Map<String,String> testParam2 = new HashMap<String,String>(){{
            put("param2","test2");
        }};

        final SEOView view1 = new MockSOEView(testParam1);
        final SEOView view2 = new MockSOEView(testParam2);

        Map<String,SEOView> viewList = new HashMap<String,SEOView>(){{
            put("view1",view1);
            put("view2",view2);
        }};

        factory.setViews(viewList);

        assertFalse(factory.isCached("view1",testParam1));
        assertFalse(factory.isCached("view2",testParam2));

        String resultHtml1 = factory.getHtml("view1",testParam1);
        assertEquals(resultHtml1, getMockHTML(testParam1));
        assertTrue(factory.isCached("view1",testParam1));
        assertFalse(factory.isCached("view2",testParam2));
        assertEquals(resultHtml1, factory.getCachedView("view1", testParam1));

        String resultHtml2 = factory.getHtml("view2",testParam2);
        assertEquals(resultHtml2, getMockHTML(testParam2));
        assertTrue(factory.isCached("view1", testParam1));
        assertTrue(factory.isCached("view2", testParam2));
        assertEquals(resultHtml2,factory.getCachedView("view2",testParam2));
    }

    @Ignore
    @Test
    public void testGetDefaultView(){
        throw new NotImplementedException();
    }
}
