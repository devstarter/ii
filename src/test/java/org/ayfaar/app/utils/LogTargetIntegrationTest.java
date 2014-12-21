package org.ayfaar.app.utils;

import org.ayfaar.app.IntegrationTest;
import org.ayfaar.app.controllers.ItemController;
import org.ayfaar.app.controllers.NewSearchController;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;


public class LogTargetIntegrationTest extends IntegrationTest {
    @Autowired
    NewSearchController searchController;
    @Autowired
    LogTarget logTarget;
    @Inject
    ItemController itemController;

    @Test
    public void test() {
        //searchController.search("we", 0, null);
        //itemController.get("sdf");
        itemController.get("10.10176");

        List<LogTarget.MethodProvider> log = logTarget.getLog();
        for(LogTarget.MethodProvider m : log) {
            System.out.println(m.methodSignature);
            System.out.println(Arrays.toString(m.params));
        }
    }
}
