package org.ayfaar.app.sync

import au.com.bytecode.opencsv.CSVWriter
import mu.KotlinLogging
import org.ayfaar.app.utils.AyfaarRuFileTransfer
import org.ayfaar.app.utils.TermService
import org.ayfaar.app.vocabulary.VocabularyService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.StringWriter
import javax.inject.Inject


@Component
class AyfaarRuVocabularySync {
    private val logger = KotlinLogging.logger {  }
    @Inject lateinit var termService: TermService
    @Inject lateinit var vocabularyService: VocabularyService
    @Inject lateinit var fileTransfer: AyfaarRuFileTransfer

    @Scheduled(cron = "0 0 3 * * *")  // every day at 3:00
    fun sync() {
        logger.info { "Sync started" }
        val data = getModel()
        val stringWriter = StringWriter()
        val writer = CSVWriter(stringWriter)
        writer.writeAll(data.tolStringArray())
        writer.close()

        logger.info { "Uploading data..." }
        fileTransfer.upload("test.csv", stringWriter.toString())
        logger.info { "Uploading done. Sync finish" }
    }

    private fun getModel(): List<AyfaarRuVocabularyItem> {
        val vocabularyTerms = vocabularyService.getTerms().also {
            logger.info { "Vocabulary loaded with ${it.size} terms" }
        }
        return termService.all
                .map { it.value to it.value.mainOrThis.term }
                .distinctBy { (_, term) -> term.name }
                .sortedBy { (_, term) -> term.name }
                .map { (provider, term) ->
                    val vTerm = vocabularyTerms.find { vTerm -> vTerm.name == term.name }
                    AyfaarRuVocabularyItem(
                            term = term.name,
                            source = vTerm?.source,
                            zkk = vTerm?.zkk,
                            aliases = vTerm?.aliases?.map { it.name } ?: emptyList(),
                            reductions = vTerm?.reductions ?: emptyList(),
                            short = term.shortDescription,
                            long = term.description,
                            vocabulary = vTerm?.description,
                            related = provider.related.map { it.name })
                }.also {
                    logger.info { "Map with  ${it.size} terms ready" }
                }
    }
}

private fun List<AyfaarRuVocabularyItem>.tolStringArray() = map {
    arrayOf(it.term,
            it.source,
            it.zkk,
            it.short,
            it.vocabulary,
            it.long,
            it.reductions.joinToString(";"),
            it.aliases.joinToString(";"),
            it.related.joinToString(";")
    )
}

data class AyfaarRuVocabularyItem(
        val term: String,
        val source: String?,
        val zkk: String?,
        val short: String?,
        val vocabulary: String?,
        val long: String?,
        val reductions: Collection<String>,
        val aliases: Collection<String>,
        val related: Collection<String>
)
