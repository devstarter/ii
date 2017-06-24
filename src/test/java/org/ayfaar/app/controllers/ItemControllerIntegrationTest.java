package org.ayfaar.app.controllers;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.TermService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ItemControllerIntegrationTest extends IntegrationTest {
    @Autowired ItemController itemController;
    @Autowired TermService termService;

    @Test
    public void testGetLinkedTerms() {
        List<Term> terms = (List)itemController.getLinkedTerms("2.0125");

        assertTrue(terms.size() > 0);
        assertEquals("ВСЕ-Воля-ВСЕ-Разума", terms.get(0).getName());
        assertEquals("Чистое Качество", terms.get(terms.size()-1).getName());
    }
}
