package org.ayfaar.app.utils.contents;


import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContentsBuilder {

    @Autowired
    CategoryDao categoryDao;

    List<Category> tomCategories = new ArrayList<Category>();
    List<Category> sectionCategories = new ArrayList<Category>();
    List<Category> chapterCategories = new ArrayList<Category>();
    List<Category> paragraphCategories = new ArrayList<Category>();

    public List<Category> getTomCategories() {
        return tomCategories;
    }

    public void setTomCategories(List<Category> tomCategories) {
        this.tomCategories = tomCategories;
    }

    public List<Category> getSectionCategories() {
        return sectionCategories;
    }

    public void setSectionCategories(List<Category> sectionCategories) {
        this.sectionCategories = sectionCategories;
    }

    public List<Category> getChapterCategories() {
        return chapterCategories;
    }

    public void setChapterCategories(List<Category> chapterCategories) {
        this.chapterCategories = chapterCategories;
    }

    public List<Category> getParagraphCategories() {
        return paragraphCategories;
    }

    public void setParagraphCategories(List<Category> paragraphCategories) {
        this.paragraphCategories = paragraphCategories;
    }

    public void createContents() {
        List<Category> topCategories = categoryDao.getTopLevel();

        setTomCategories(getCategory(topCategories));
        setSectionCategories(getCategory(tomCategories));
        setChapterCategories(getCategory(sectionCategories));
        setParagraphCategories(getCategory(chapterCategories));

        List<Section> sections = createSection(sectionCategories);
        List<Chapter> chapters = createChapter(chapterCategories);
        Section section = new Section();
        section.setChapters(chapters);

    }

    public List<Section> createSection(List<Category> sectionCategories) {
        List<Section> sections = new ArrayList<Section>();
        for(Category category : sectionCategories) {
            Section section = new Section();
            section.setName(category.getName());
            section.setDescription(category.getDescription());
            sections.add(section);
        }
        return sections;
    }

    public List<Chapter> createChapter(List<Category> chapterCategories) {
        List<Chapter> chapters = new ArrayList<Chapter>();
        for(Category category : chapterCategories) {
            Chapter chapter = new Chapter();
            chapter.setName(category.getName());
            chapter.setDescription(category.getDescription());
            chapters.add(chapter);
        }
        return chapters;
    }

    public List<Category> getCategory(List<Category> topCategories) {
        List<Category> categories = new ArrayList<Category>();
        for (Category category : topCategories) {
            for (Category child : getChildren(category)) {
                categories.add(child);
            }
        }
        return categories;
    }


    private List<Category> getChildren(Category parent) {
        //System.out.println("parent " + parent.getName());
        List<Category> children = new ArrayList<Category>();
        if (parent.getStart() != null) {
            //System.out.println("start " + parent.getStart());
            Category child = categoryDao.get(parent.getStart());
            //System.out.println("children " + child.getName());
            if (child != null) {
                children.add(child);
                while (child.getNext() != null) {
                    child = categoryDao.get(child.getNext());
                    //System.out.println("child " + child.getName());
                    //System.out.println("child parent " + child.getParent() + " parent uri " + parent.getUri());
                    if (!child.getParent().equals(parent.getUri()))
                        break;
                    children.add(child);
                }
            }
        }
        return children;
    }
}
