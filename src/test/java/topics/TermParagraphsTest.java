package topics;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.TermParagraph;
import org.ayfaar.app.services.itemRange.ItemRangeServiceImpl;
import org.ayfaar.app.utils.ContentsService;
import org.ayfaar.app.utils.TermService;
import org.ayfaar.app.utils.TermsFinder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.stream.Stream;

@ActiveProfiles("remote")
public class TermParagraphsTest extends IntegrationTest {

    @Autowired
    ItemRangeServiceImpl itemRangeServiceImpl;
    @Autowired
    TermsFinder termsFinder;
    @Autowired
    CommonDao commonDao;
    @Autowired
    ContentsService contentsService;
    @Autowired
    TermService termService;

    @Test
    public void TermParagraphTest(){//ONLY FOR SAVE TERM-PARAGRAPH TO DB (NOT TEST)
        contentsService.getAllParagraphs().forEach(o1 -> saveTermParagraph(o1.code(),o1.description()));
    }

    private void saveTermParagraph(String code, String paragraph){

        Map<String, Integer> termsWithFrequency = termsFinder.getTermsWithFrequency(paragraph);

        termsWithFrequency.keySet().parallelStream().map(term ->
                new TermParagraph(term, code)).forEach(t ->
                commonDao.save(TermParagraph.class,t));
    }

    @Test
    public void getParagraphsByTerm(){
        Stream<String> paragraphsByTerm = itemRangeServiceImpl.getParagraphsByMainTerm("Время");
        paragraphsByTerm.forEach(System.out::println);
    }
}
