package org.ayfaar.app;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

public abstract class AbstractTest {

    public String getFile(String fileName) throws IOException {
        return IOUtils.toString(this.getClass().getResourceAsStream(fileName));
    }
}
