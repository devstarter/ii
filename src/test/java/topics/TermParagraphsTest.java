package topics;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.TermParagraph;
import org.ayfaar.app.services.TermParagraphService;
import org.ayfaar.app.utils.ContentsService;
import org.ayfaar.app.utils.TermsFinder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

public class TermParagraphsTest extends IntegrationTest {

    @Autowired
    TermParagraphService termParagraphService;
    @Autowired
    TermsFinder termsFinder;
    @Autowired
    CommonDao commonDao;
    @Autowired
    ContentsService contentsService;

    @Test
    public void TermParagraphTest(){
        contentsService.getAllParagraphs().forEach(o1 -> saveTermParagraph(o1.code(),o1.description()));
    }

    private void saveTermParagraph(String code, String paragraph){

        Map<String, Integer> termsWithFrequency = termsFinder.getTermsWithFrequency(paragraph);

        termsWithFrequency.keySet().stream().map(term ->
                                createTermParagraph(code, term)).forEach(t ->
                                commonDao.save(TermParagraph.class,t));
    }

    private TermParagraph createTermParagraph(String code, String term) {

        TermParagraph termParagraph = new TermParagraph();
        termParagraph.setParagraph(code);
        termParagraph.setTerm(term);

        return termParagraph;
    }

    @Test
    public void getParagraphsByTerm(){
        List<String> paragraphsByTerm = termParagraphService.getParagraphsByTerm("Время");
        paragraphsByTerm.stream().forEach(System.out::println);
    }
}
