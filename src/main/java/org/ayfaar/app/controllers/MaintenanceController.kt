package org.ayfaar.app.controllers

import org.ayfaar.app.services.EntityLoader
import org.ayfaar.app.sync.GetVideosFormYoutube
import org.ayfaar.app.sync.RecordSynchronizer
import org.ayfaar.app.sync.TermDescriptionImporter
import org.ayfaar.app.sync.VocabularySynchronizer
import org.ayfaar.app.translation.TopicTranslationSynchronizer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject

@RestController
@RequestMapping("api")
class MaintenanceController @Inject
constructor(val entityLoader: EntityLoader,
            val recordSynchronizer: RecordSynchronizer,
            val topicTranslationSynchronizer: TopicTranslationSynchronizer,
            val getVideosFormYoutube: GetVideosFormYoutube,
            val termDescriptionImporter: TermDescriptionImporter) {


    @Autowired(required = false) var vocabularySynchronizer: VocabularySynchronizer? = null

    @RequestMapping("entity-loader/clear")
    fun clearEntityLoader() {
        entityLoader.clear()
    }

    @RequestMapping("sync/records")
    fun synchronizeRecords() {
        recordSynchronizer.synchronize()
    }

    @RequestMapping("sync/translations")
    fun synchronizeTranslations() {
        topicTranslationSynchronizer.synchronize()
    }

    @RequestMapping("sync/videos")
    fun synchronizeVideos() {
        getVideosFormYoutube.synchronize()
    }

    @RequestMapping("sync/vocabulary")
    fun synchronizeVocabulary() {
        vocabularySynchronizer?.synchronize() ?: throw RuntimeException("VocabularySynchronizer available only in production")
    }

    @RequestMapping("sync/import-term-descriptions")
    fun importTermDescriptions() {
        termDescriptionImporter.import()
    }
}
