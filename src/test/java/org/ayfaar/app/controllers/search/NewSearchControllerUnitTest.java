package org.ayfaar.app.controllers.search;

import org.ayfaar.app.controllers.NewSearchController;
import org.ayfaar.app.dao.SearchDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NewSearchControllerUnitTest {

    @Mock SearchDao searchDao;
    @InjectMocks NewSearchController controller;

    @Test
    public void testHasMore() {

    }
}
