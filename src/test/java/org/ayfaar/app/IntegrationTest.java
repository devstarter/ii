package org.ayfaar.app;

import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringTestConfiguration.class)
public abstract class IntegrationTest {
    public String getFile(String fileName) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(fileName));
    }
}
