package org.ayfaar.app.controllers;

import org.ayfaar.app.services.EntityLoader;
import org.ayfaar.app.sync.RecordSynchronizer;
import org.ayfaar.app.translation.TopicTranslationSynchronizer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.IOException;

@RestController
@RequestMapping("api")
public class MaintenanceController {
    @Inject EntityLoader entityLoader;
    @Inject RecordSynchronizer recordSynchronizer;
    @Inject TopicTranslationSynchronizer topicTranslationSynchronizer;

    @RequestMapping("entity-loader/clear")
    public void clearEntityLoader() {
        entityLoader.clear();
    }

    @RequestMapping("sync/records")
    public void synchronizeRecords() throws IOException {
        recordSynchronizer.synchronize();
    }

    @RequestMapping("sync/translations")
    public void synchronizeTranslations() throws IOException {
        topicTranslationSynchronizer.synchronize();
    }
}
