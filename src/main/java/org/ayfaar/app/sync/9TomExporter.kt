package org.ayfaar.app.sync

import org.ayfaar.app.dao.ArticleDao
import org.ayfaar.app.services.GoogleSpreadsheetService
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
class `9TomExporter` {
    @Inject lateinit var articleDao: ArticleDao
    @Inject lateinit var spreadsheetService: GoogleSpreadsheetService

    fun sync() {
        val synchronizer = GoogleSpreadsheetSynchronizer
                .build<TomSyncItem>(spreadsheetService, "1fGg6sriTLm2PLRaKqaeyRomrvy-AphU8mTl7LyMCaD8")
                    .keyGetter { it.name }
                    .skipFirstRow()
                    .ignoreCase()
                    .localDataLoader { getData() }
                    .build()
        synchronizer.sync()
    }

    private fun getData() = articleDao.all
            .map { TomSyncItem(it.name, it.content) }

}

data class TomSyncItem(
        val name: String,
        val content: String
) : SyncItem {
    override fun toRaw() = listOf(name, content)
}
