package org.ayfaar.app.controllers

import org.ayfaar.app.services.EntityLoader
import org.ayfaar.app.sync.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject

@RestController
@RequestMapping("api")
class MaintenanceController @Inject
constructor(val entityLoader: EntityLoader,
            val getVideosFormYoutube: GetVideosFormYoutube,
            val ayfaarRuNavigatorUpdater: AyfaarRuNavigatorUpdater,
            val termDescriptionImporter: TermDescriptionImporter) {


    @Autowired(required = false) var vocabularySynchronizer: VocabularySynchronizer? = null
    @Autowired(required = false) var recordSynchronizer: RecordSynchronizer? = null

    @RequestMapping("entity-loader/clear")
    fun clearEntityLoader() {
        entityLoader.clear()
    }

    @RequestMapping("sync/records")
    fun synchronizeRecords() {
        recordSynchronizer?.synchronize() ?: throw RuntimeException("Not available on dev profile")
    }
    /*
    @RequestMapping("sync/translations")
    fun synchronizeTranslations() {
        topicTranslationSynchronizer.synchronize()
    }*/

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

    @RequestMapping("sync/update-navigator")
    fun updateNavigator() {
        ayfaarRuNavigatorUpdater.sync()
    }
}
