package org.ayfaar.app.sync

import org.ayfaar.app.services.GoogleSpreadsheetService
import org.ayfaar.app.utils.TermService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class AbbreviationExporter {
    @Inject lateinit var termService: TermService
    @Inject lateinit var spreadsheetService: GoogleSpreadsheetService

    @Scheduled(cron = "0 0 * * * *")  // every hour
    fun sync() {
        val synchronizer = GoogleSpreadsheetSynchronizer.build<AbbreviationSyncItem>(spreadsheetService, "1sX_zZGPRMD2o-6nqeTttuKFYTr4vrb6ZpOVchpY0hZg")
                .keyGetter { it.abbreviation }
                .skipFirstRow()
                .ignoreCase()
                .localDataLoader { getAbbreviationMap() }
                .build()
        synchronizer.sync()
    }

    private fun getAbbreviationMap() = termService.all
            .map { it.value }
            .filter { it.isAbbreviation }
            .distinctBy { it.name }
            .reversed()
            .map { AbbreviationSyncItem(it.name, it.mainOrThis.name) }

}

data class AbbreviationSyncItem(
        val abbreviation: String,
        val term: String
) : SyncItem {
    override fun toRaw() = listOf(abbreviation, term)
}
