package org.ayfaar.app.sync

import mu.KotlinLogging
import org.ayfaar.app.services.GoogleSpreadsheetService
import org.ayfaar.app.utils.TermService
import org.ayfaar.app.vocabulary.VocabularyService
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class TermDetailsExporter {
    private val logger = KotlinLogging.logger {  }
    @Inject lateinit var termService: TermService
    @Inject lateinit var spreadsheetService: GoogleSpreadsheetService
    @Inject lateinit var vocabularyService: VocabularyService

//    @Scheduled(cron = "0 0 * * * *")  // every hour
    fun sync() {
        logger.info { "Sync started" }
        val synchronizer = GoogleSpreadsheetSynchronizer.build<TermDetailsSyncItem>(spreadsheetService, "1s98bAG5N77uOKYpieDIg6ddYbL9JDHZkHlPb-1_HFKE")
                .keyGetter { it.term }
                .skipFirstRow()
                .ignoreCase()
                .localDataLoader { getTermsMap() }
                .build()
        logger.info { "Uploading data..." }
        synchronizer.sync()
        logger.info { "Sync finish" }
    }

    private fun getTermsMap(): List<TermDetailsSyncItem> {
        val vocabularyTerms = vocabularyService.getTerms().also {
            logger.info { "Vocabulary loaded with ${it.size} terms" }
        }
        return termService.all
                .map { it.value.mainOrThis.term }
                .distinctBy { it.name }
                .sortedBy { it.name }
                .map {
                    val vTerm = vocabularyTerms.find { vTerm -> vTerm.name == it.name }
                    TermDetailsSyncItem(
                            term = it.name,
                            short = it.shortDescription,
                            long = it.description,
                            vocabulary = vTerm?.description)
                }.also {
                    logger.info { "Map with  ${it.size} terms ready" }
                }
    }

}

data class TermDetailsSyncItem(
        val term: String,
        val short: String?,
        val long: String?,
        val vocabulary: String?
) : SyncItem {
    override fun toRaw() = listOf(term, short, long, vocabulary)
}
