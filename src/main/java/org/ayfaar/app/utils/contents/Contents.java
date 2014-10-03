package org.ayfaar.app.utils.contents;


import org.ayfaar.app.model.Category;

import java.util.Calendar;
import java.util.List;

public class Contents {
    String name;
    private List<Section> sections;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*public List<Category> getSections() {
        return sections;
    }

    public void setSections(List<Category> sections) {
        this.sections = sections;
    }*/

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }
}
