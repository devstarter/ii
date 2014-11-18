package org.ayfaar.app.controllers;


import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.TermMorph;
import org.ayfaar.app.utils.NewAliasesMap;
import org.ayfaar.app.utils.TermsMap;
import org.ayfaar.app.utils.UriGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class TermsControllerIntegrationTest  {
    @Mock TermDao termDao;
    @Mock CommonDao commonDao;
    @Mock TermsMap termsMap;
    @Mock NewAliasesMap aliasesMap;
    @InjectMocks
    @Spy
    TermController termController;


    /**
     * test adding aliases to TermsMap
     */
    @Test
    public void testFindAliasesMethod() {
        Term term = new Term("тест");
        term.setUri(UriGenerator.generate(Term.class, term.getName()));

        doReturn(term).when(termDao).save(any(Term.class));
        doReturn(null).when(commonDao).get(eq(TermMorph.class), anyString());

        termController.add(term.getName(), "description");

        verify(termsMap, times(9)).put(anyString(), any(TermsMap.TermProvider.class));
    }
}
