package org.ayfaar.app.controllers;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.TermsMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ItemControllerIntegrationTest extends IntegrationTest {
    @Autowired ItemController itemController;
    @Autowired TermsMap termsMap;

    @Test
    public void testGetLinkedTerms() {
        List<Term> terms = (List)itemController.getLinkedTerms("2.0125");

        assertEquals(12, terms.size());
        assertEquals("ВСЕ-Воля-ВСЕ-Разума", terms.get(0).getName());
        assertEquals("Чистое Качество", terms.get(terms.size()-1).getName());
    }
}
