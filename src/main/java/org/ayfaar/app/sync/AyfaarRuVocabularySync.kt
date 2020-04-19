package org.ayfaar.app.sync

import com.google.gson.Gson
import mu.KotlinLogging
import org.ayfaar.app.utils.AyfaarRuFileTransfer
import org.ayfaar.app.utils.TermService
import org.ayfaar.app.vocabulary.VocabularyService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
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
//        val stringWriter = StringWriter()
//        val writer = CSVWriter(stringWriter)
//        writer.writeAll(data.tolStringArray())
//        writer.close()
        val json = Gson().toJson(data)

        logger.info { "Uploading data..." }
        fileTransfer.upload("/src/vocabulary/vocabulary.json", json)
        logger.info { "Uploading done. Sync finish" }
    }

    private fun getModel(): List<AyfaarRuVocabularyItem> {
        val vocabularyTerms = vocabularyService.getTerms().also {
            logger.info { "Vocabulary loaded with ${it.size} terms" }
        }
        val myTermsFirst = termService.all
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
                }

        val termsNotInMyBase = vocabularyTerms
                .filter { !termService.get(it.name).isPresent || termService.get(it.name).get().mainOrThis.name != it.name }
                .map { term ->
                    AyfaarRuVocabularyItem(
                            term = term.name,
                            source = term.source,
                            zkk = term.zkk,
                            aliases = term.aliases.map { it.name } ,
                            reductions = term.reductions,
                            vocabulary = term.description,
                            related = emptyList())
                }
        return myTermsFirst + termsNotInMyBase
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
        val short: String? = null,
        val vocabulary: String?,
        val long: String? = null,
        val reductions: Collection<String>,
        val aliases: Collection<String>,
        val related: Collection<String>
)
