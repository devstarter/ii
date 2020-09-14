package org.ayfaar.app.sync

import mu.KotlinLogging
import org.ayfaar.app.controllers.TopicController
import org.ayfaar.app.services.GoogleSpreadsheetService
import org.ayfaar.app.services.record.RecordService
import org.ayfaar.app.services.videoResource.VideoResourceService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class VideoExporter {
    private val logger = KotlinLogging.logger {  }
    @Inject lateinit var videoResourceService: VideoResourceService
    @Inject lateinit var spreadsheetService: GoogleSpreadsheetService
    @Inject lateinit var topicController: TopicController

    @Scheduled(cron = "0 0 5 * * ?") // at 2 AM every day
    // at 2 AM every day
    fun sync() {
        logger.info { "Sync started" }
        val synchronizer = GoogleSpreadsheetSynchronizer.build<VideoSyncItem>(spreadsheetService, "1jE_cvgIZbBOZvS9J2Jy4yIwxUTDz969BdpUPuW9dJwc")
                .keyGetter { it.code }
                .skipFirstRow()
                .ignoreCase()
                .localDataLoader { getTermsMap() }
                .build()
        logger.info { "Uploading data..." }
        synchronizer.sync()
        logger.info { "Sync finish" }
    }

    private fun getTermsMap(): List<VideoSyncItem> {
        return videoResourceService.all
                .sortedBy { it.code }
                .map {
                    val keys = topicController.getForUri(it.uri).joinToString(", ") { it.name }
                    VideoSyncItem(
                            url = "https://youtu.be/${it.id}",
                            title = it.title,
                            code = it.code,
                            keys = keys)
                }.also {
                    logger.info { "Map with  ${it.size} records ready" }
                }
    }

}

data class VideoSyncItem(
        val url: String,
        val code: String,
        val title: String,
        val keys: String
) : SyncItem {
    override fun toRaw() = listOf(url, title, code, keys)
}
