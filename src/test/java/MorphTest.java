import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class MorphTest {
    @Test
    public void test() throws IOException {
        LuceneMorphology luceneMorph = new RussianLuceneMorphology();
        List<String> wordBaseForms = luceneMorph.getMorphInfo("меркавгнация");
        for (String word : wordBaseForms) {
            System.out.println(word);
        }

    }

}
