package org.ayfaar.app.sync

import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.overzealous.remark.Remark
import org.ayfaar.app.dao.TermDao
import org.ayfaar.app.event.EventPublisher
import org.ayfaar.app.event.SysLogEvent
import org.ayfaar.app.model.Term
import org.ayfaar.app.services.EntityLoader
import org.ayfaar.app.utils.GoogleService
import org.ayfaar.app.utils.TermsMarker
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
    val myName = "Обновление словарных статей"

    @Scheduled(cron = "0 0 * * * *") // every hour
    fun import() {
        val service = getService()
        termDao.getAllWithDescriptionGid().parallelStream().forEach {
            val file: File = service.files().get(it.descriptionGid).setFields("*").execute()

            val weHaveStaleVersion = it.descriptionGVersion == null || file.version != it.descriptionGVersion

            if (weHaveStaleVersion) {
                updateDescription(it, file)
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
        val simplified = Remark().convert(html)

        term.description = simplified
        term.taggedDescription = marker.mark(simplified)
        term.descriptionGVersion = file.version
        termDao.save(term)
        loader.clear(term)
        eventPublisher.publishEvent(SysLogEvent(myName,
                String.format("Обновилась словарная статья для термина <uri>%s</uri>, на основании <a href='https://docs.google.com/document/d/%s/edit' target='_blank'>документа</a>", term.uri, term.descriptionGid),
                LogLevel.INFO))
    }
}