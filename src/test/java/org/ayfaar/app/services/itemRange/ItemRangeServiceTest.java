package org.ayfaar.app.services.itemRange;

import org.ayfaar.app.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

public class ItemRangeServiceTest extends IntegrationTest {

    @Inject private ItemRangeService itemRangeService;

    @Test
    public void test_ЕСИП() {
        final List<String> paragraphCodes = itemRangeService.getParagraphsByMainTerm("Единый Суперуниверсальный Импульс-Потенциал").toList();

        Assert.assertTrue(paragraphCodes.contains("1.4.1.9"));
        Assert.assertTrue(paragraphCodes.contains("3.11.2.2"));
        Assert.assertTrue(paragraphCodes.contains("3.11.2.3"));
        Assert.assertTrue(paragraphCodes.contains("4.13.1.2"));
        Assert.assertTrue(paragraphCodes.contains("4.13.1.3"));
        Assert.assertTrue(paragraphCodes.contains("4.13.1.10"));
        Assert.assertTrue(paragraphCodes.contains("4.15.2.1"));
        Assert.assertTrue(paragraphCodes.contains("4.16.1.1"));
        Assert.assertTrue(paragraphCodes.contains("4.16.2.31"));
    }
}