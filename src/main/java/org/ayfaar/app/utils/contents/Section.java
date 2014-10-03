package org.ayfaar.app.utils.contents;


import java.util.List;

public class Section {
    private String name;
    private String description;
    private List<Chapter> chapters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

    public String toString() {
        return getName() + getDescription();
    }
}
