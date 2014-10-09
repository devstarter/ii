package issues.issue63;


import org.apache.commons.io.IOUtils;
import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.ContentsController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestContentsControllers extends IntegrationTest{
    @Autowired
    private ContentsController contentsController;

    private String[] subCategoryTom10;
    private String[] subCategorySectionII;
    private String[] subCategoryChapter3;

    @Before
    public void init() throws IOException {
        String tom = IOUtils.toString(this.getClass().getResourceAsStream("subCategoryTom10.txt"));
        String section = IOUtils.toString(this.getClass().getResourceAsStream("subCategorySectionII.txt"));
        String chapter = IOUtils.toString(this.getClass().getResourceAsStream("subCategoryChapter3.txt"));
        subCategoryTom10 = tom.split("\n");
        subCategorySectionII = section.split("\n");
        subCategoryChapter3 = chapter.split("\n");
    }

    @Test
    public void testGetContentsWhenParentIsTom() {
        List<String> tom10Contents = contentsController.getContents("Том 10");
        print(tom10Contents);

        assertEquals(31, tom10Contents.size());
        assertEquals(subCategoryTom10[0].trim(), tom10Contents.get(0));
        assertEquals(subCategoryTom10[1].trim(), tom10Contents.get(1));
        assertEquals(subCategoryTom10[2].trim(), tom10Contents.get(2));
        assertEquals(subCategoryTom10[27].trim(), tom10Contents.get(27));
        assertEquals(subCategoryTom10[30].trim(), tom10Contents.get(30));
    }

    @Test
    public void testGetContentsWhenParentIsSection() {
        List<String> sectionIIContents = contentsController.getContents("БДК / Раздел II");
        print(sectionIIContents);

        assertEquals(9, sectionIIContents.size());
        assertEquals(subCategorySectionII[0].trim(), sectionIIContents.get(0));
        assertEquals(subCategorySectionII[1].trim(), sectionIIContents.get(1));
        assertEquals(subCategorySectionII[2].trim(), sectionIIContents.get(2));
        assertEquals(subCategorySectionII[7].trim(), sectionIIContents.get(7));
    }

    @Test
    public void testGetContentsWhenParentIsChapter() {
        List<String> chapter3Contents = contentsController.getContents("БДК / Раздел I / Глава 3");
        print(chapter3Contents);

        assertEquals(61, chapter3Contents.size());
        assertEquals(subCategoryChapter3[0].trim(), chapter3Contents.get(0));
        assertEquals(subCategoryChapter3[1].trim(), chapter3Contents.get(1));
        assertEquals(subCategoryChapter3[2].trim(), chapter3Contents.get(2));
        assertEquals(subCategoryChapter3[59].trim(), chapter3Contents.get(59));
        assertEquals(subCategoryChapter3[60].trim(), chapter3Contents.get(60));
    }

    private void print(List<String> categories) {
        for(String s : categories) {
            System.out.println(s);
        }
        System.out.println();
        System.out.println();
    }
}
