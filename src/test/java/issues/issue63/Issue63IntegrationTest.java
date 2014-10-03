package issues.issue63;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.utils.contents.Contents;
import org.ayfaar.app.utils.contents.ContentsBuilder;
import org.ayfaar.app.utils.contents.Section;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


public class Issue63IntegrationTest extends IntegrationTest {
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    ContentsBuilder contentsBuilder;


    @Test
    public void testTom() {
        contentsBuilder.createContents();
        List<Category> topCategories = categoryDao.getTopLevel();
        List<Category> toms = contentsBuilder.getTomCategories();
        List<Category> sections = contentsBuilder.getSectionCategories();
        List<Category> chapters= contentsBuilder.getChapterCategories();
        List<Category> paragraphs = contentsBuilder.getParagraphCategories();

        System.out.println("toms " + toms.size());
        System.out.println("sections " + sections.size());
        System.out.println("chapters " + chapters.size());
        System.out.println("paragraphs " + paragraphs.size());


        Contents contents = new Contents();
        /*contents.setName(toms.get(0).getName());
        contents.setSections(sec);*/
        //contents.setSections(sections);

        System.out.println(contents.getName());
       /* for(Section c : contents.getSections()) {
            System.out.println(c.toString());
        }*/
        //contentsBuilder.createToms();
        /*List<Category> list = contentsBuilder.getTom();
        System.out.println(list.size());
        for(Category c : list) {
            System.out.println(c.getName());
        }*/
    }

}
