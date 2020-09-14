package org.ayfaar.app.sync

import mu.KotlinLogging
import org.ayfaar.app.controllers.TopicController
import org.ayfaar.app.services.GoogleSpreadsheetService
import org.ayfaar.app.services.record.RecordService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class RecordExporter {
    private val logger = KotlinLogging.logger {  }
    @Inject lateinit var recordService: RecordService
    @Inject lateinit var spreadsheetService: GoogleSpreadsheetService
    @Inject lateinit var topicController: TopicController

    @Scheduled(cron = "0 0 4 * * ?") // at 2 AM every day
    // at 2 AM every day
    fun sync() {
        logger.info { "Sync started" }
        val synchronizer = GoogleSpreadsheetSynchronizer.build<RecordSyncItem>(spreadsheetService, "1aanHSSeubgdz9_Ok1NopNfvl--KT6aIQBZQ0NAovNB8")
                .keyGetter { it.code }
                .skipFirstRow()
                .ignoreCase()
                .localDataLoader { getTermsMap() }
                .build()
        logger.info { "Uploading data..." }
        synchronizer.sync()
        logger.info { "Sync finish" }
    }

    private fun getTermsMap(): List<RecordSyncItem> {
        val records = recordService.all.also {
            logger.info { "Records loaded ${it.size}" }
        }
        return recordService.all
                .sortedBy { it.code }
                .map {
                    val keys = topicController.getForUri(it.uri).joinToString(", ") { it.name }
                    RecordSyncItem(
                            code = it.code,
                            name = it.name,
                            keys = keys)
                }.also {
                    logger.info { "Map with  ${it.size} records ready" }
                }
    }

}

data class RecordSyncItem(
        val code: String,
        val name: String,
        val keys: String
) : SyncItem {
    override fun toRaw() = listOf(code, name, keys)
}
