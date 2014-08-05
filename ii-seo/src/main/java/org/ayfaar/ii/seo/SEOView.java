package org.ayfaar.ii.seo;

import java.util.Map;

/**
 * Created by Drorzz on 04.08.2014.
 */
public interface SEOView {
    public void setViewParameters(Map<String,String> viewParameters);
    public String getHTML();
}
