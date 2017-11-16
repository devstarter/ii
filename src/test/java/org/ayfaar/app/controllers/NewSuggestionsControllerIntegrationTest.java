package org.ayfaar.app.controllers;

import org.ayfaar.app.IntegrationTest;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class NewSuggestionsControllerIntegrationTest extends IntegrationTest {
    @Inject
    NewSuggestionsController suggestionsController;

    @Test
    public void test_квант() {
        String query = "квант";
        Collection<String> suggestions = suggestionsController.suggestTerms(query);
        assertTrue(suggestions.contains("квант"));
        // TODO: проверить что этот элемент является первым в списке
    }
}
