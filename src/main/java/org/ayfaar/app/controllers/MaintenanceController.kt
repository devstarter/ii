package org.ayfaar.app.controllers

import mu.KotlinLogging
import org.ayfaar.app.services.EntityLoader
import org.ayfaar.app.sync.*
import org.ayfaar.app.utils.TermsTaggingUpdater
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
            val abbreviationExporter: AbbreviationExporter,
            val termDescriptionImporter: TermDescriptionImporter,
            val termDetailsExporter: TermDetailsExporter,
            val termsTaggingUpdater: TermsTaggingUpdater,
            val `9TomExporter`: `9TomExporter`,
            val recordExporter: RecordExporter,
            val videoExporter: VideoExporter,
            val ayfaarRuVocabularySync: AyfaarRuVocabularySync) {

    private val logger = KotlinLogging.logger {}

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

    @RequestMapping("sync/records2")
    fun synchronizeRecords2() {
        recordExporter.sync()
    }

    @RequestMapping("sync/video")
    fun synchronizeVideo() {
        videoExporter.sync()
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

    @RequestMapping("sync/export-abbreviations")
    fun exportAbbreviations() {
        abbreviationExporter.sync()
    }

    @RequestMapping("sync/term-details")
    fun exportTermDetails() {
        termDetailsExporter.sync()
    }

    @RequestMapping("sync/9tom")
    fun export9Tom() {
        `9TomExporter`.sync()
    }

    @RequestMapping("sync/ayfaar-ru-vocabulary")
    fun ayfaaRuVocabularySync() {
        ayfaarRuVocabularySync.sync()
    }

    @RequestMapping("update-all-tags")
    fun updateAllTags() {
        Thread {
            logger.info { "Update all items..." }
            termsTaggingUpdater.updateAllContent()
            logger.info { "Update all terms..." }
            termsTaggingUpdater.updateAllTerms()
            logger.info { "Update all quotes..." }
            termsTaggingUpdater.updateAllQuotes()
        }.start()
    }
}
