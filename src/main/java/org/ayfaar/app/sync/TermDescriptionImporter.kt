package org.ayfaar.app.sync

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import mu.KotlinLogging
import org.ayfaar.app.dao.TermDao
import org.ayfaar.app.event.EventPublisher
import org.ayfaar.app.event.SysLogEvent
import org.ayfaar.app.model.Term
import org.ayfaar.app.services.EntityLoader
import org.ayfaar.app.utils.GoogleService
import org.ayfaar.app.utils.TermsMarker
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.safety.Whitelist
import org.springframework.boot.logging.LogLevel
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.util.MimeTypeUtils
import java.io.ByteArrayOutputStream
import javax.inject.Inject



@Service
@EnableScheduling
//@Profile("default")
class TermDescriptionImporter @Inject constructor(val termDao: TermDao,
                                                  val marker: TermsMarker,
                                                  val loader: EntityLoader,
                                                  val eventPublisher: EventPublisher) {
    private val logger = KotlinLogging.logger {}

    val myName = "Обновление словарных статей"

    @Scheduled(cron = "0 0 * * * *") // every hour
    fun import() {
        val service = getService()
        termDao.allWithDescriptionGid.parallelStream().forEach {
            val file: File = service.files().get(it.descriptionGid).setFields("*").execute()

            val weHaveStaleVersion = it.descriptionGVersion == null || file.version != it.descriptionGVersion

            if (weHaveStaleVersion) try {
                updateDescription(it, file)
            } catch (e: Exception) {
                logger.error( e, { "TermDescriptionImporter error while import description for term ${it.name} (doc id: ${it.descriptionGid})" })
            }
        }
    }

    private fun getService(): Drive {
        return GoogleService.getDriveService()
    }

    private fun updateDescription(term: Term, file: File) {
        val outputStream = ByteArrayOutputStream()
        getService().files().export(term.descriptionGid, MimeTypeUtils.TEXT_HTML_VALUE).executeAndDownloadTo(outputStream)

        val html = outputStream.toString()
        val simplified = cleanPreserveLineBreaks(html) //Remark().convert(html)

        term.description = simplified
        term.taggedDescription = marker.mark(simplified, true)
        term.descriptionGVersion = file.version
        termDao.save(term)
        loader.clear(term)
        eventPublisher.publishEvent(SysLogEvent(myName,
                String.format("Обновилась словарная статья для термина <uri>%s</uri>, на основании <a href='https://docs.google.com/document/d/%s/edit' target='_blank'>документа</a>", term.uri, term.descriptionGid),
                LogLevel.INFO))
    }

    // see https://stackoverflow.com/questions/5640334/how-do-i-preserve-line-breaks-when-using-jsoup-to-convert-html-to-plain-text
    // see https://stackoverflow.com/a/46344397/975169
    fun cleanPreserveLineBreaks(bodyHtml: String): String {
        // get pretty printed html with preserved br and p tags
        val prettyPrintedBodyFragment = Jsoup.clean(bodyHtml, "",
                Whitelist.none().addTags("br", "p", "a", "ul", "ol", "li"),
                Document.OutputSettings().prettyPrint(true))
        // get plain text with preserved line breaks by disabled prettyPrint
        return Jsoup.clean(prettyPrintedBodyFragment, "", Whitelist.none().addTags("a", "ul", "ol", "li"),
                Document.OutputSettings().prettyPrint(false))
    }
}