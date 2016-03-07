package org.ayfaar.app.controllers;


import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.model.Term;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class ArticleControllerIntegrationTest extends IntegrationTest {
    @Autowired
    private ArticleController articleController;

    @Test
    public void testGetRelatedTerms() {
        List<Term> terms = articleController.getRelatedTerms(1);

        assertEquals(7, terms.size());
        assertEquals("АИИЙВВФФ", terms.get(0).getName());
        assertEquals("Энерго-Плазма", terms.get(terms.size()-1).getName());
    }
}
